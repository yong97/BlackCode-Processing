/*
 * Copyright 2010 LIUM, based on Carnegie Mellon University previous work.  
 * Portions Copyright 2002 Sun Microsystems, Inc.  
 * Portions Copyright 2002 Mitsubishi Electric Research Laboratories.
 * Portions Copyright 2010 LIUM, University of Le Mans, France
  -> Yannick Esteve, Anthony Rousseau

 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.linguist.language.ngram.large;

import edu.cmu.sphinx.linguist.WordSequence;
import edu.cmu.sphinx.linguist.dictionary.Dictionary;
import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.linguist.language.ngram.BackoffLanguageModel;
import edu.cmu.sphinx.linguist.language.ngram.LanguageModel;
import edu.cmu.sphinx.linguist.language.ngram.ProbDepth;
import edu.cmu.sphinx.util.LogMath;
import edu.cmu.sphinx.util.TimerPool;
import edu.cmu.sphinx.linguist.util.LRUCache;
import edu.cmu.sphinx.util.props.*;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Language model that uses a binary NGram language model file ("DMP file") 
 * generated by the SphinxBase sphinx_lm_convert.
 */

public class LargeNGramModel implements LanguageModel, BackoffLanguageModel {

    /**
     * The property for the name of the file that logs all the queried N-grams. If this property is set to null, it
     * means that the queried N-grams are not logged.
     */
    @S4String(mandatory = false)
    public static final String PROP_QUERY_LOG_FILE = "queryLogFile";

    /** The property that defines that maximum number of ngrams to be cached */
    @S4Integer(defaultValue = 100000)
    public static final String PROP_NGRAM_CACHE_SIZE = "ngramCacheSize";

    /** The property that controls whether the ngram caches are cleared after every utterance */
    @S4Boolean(defaultValue = false)
    public static final String PROP_CLEAR_CACHES_AFTER_UTTERANCE = "clearCachesAfterUtterance";

    /** The property that defines the language weight for the search */
    @S4Double(defaultValue = 1.0f)
    public final static String PROP_LANGUAGE_WEIGHT = "languageWeight";

    /** The property that defines the logMath component. */
    @S4Component(type = LogMath.class)
    public final static String PROP_LOG_MATH = "logMath";

    /**
     * The property that controls whether or not the language model will apply the language weight and word insertion
     * probability
     */
    @S4Boolean(defaultValue = false)
    public final static String PROP_APPLY_LANGUAGE_WEIGHT_AND_WIP = "applyLanguageWeightAndWip";

    /** Word insertion probability property */
    @S4Double(defaultValue = 1.0f)
    public final static String PROP_WORD_INSERTION_PROBABILITY = "wordInsertionProbability";

    /** If true, use full bigram information to determine smear */
    @S4Boolean(defaultValue = false)
    public final static String PROP_FULL_SMEAR = "fullSmear";

    /**
     * The number of bytes per N-gram in the LM file generated by the CMU-Cambridge Statistical Language Modeling
     * Toolkit.
     */
    public static final int BYTES_PER_NGRAM = 4;
    public static final int BYTES_PER_NMAXGRAM = 2;
    
    private final static int SMEAR_MAGIC = 0xC0CAC01A; // things go better 

    // ------------------------------
    // Configuration data
    // ------------------------------
    URL location;
    protected Logger logger;
    protected LogMath logMath;
    protected int maxDepth;

    protected int ngramCacheSize;
    protected boolean clearCacheAfterUtterance;
    
    protected boolean fullSmear;
    
    protected Dictionary dictionary;
    protected String format;
    protected boolean applyLanguageWeightAndWip;
    protected float languageWeight;
    protected float unigramWeight;
    protected double wip;

    // -------------------------------
    // Statistics
    // -------------------------------
    private int ngramMisses;
    private int ngramHits;
    private int smearTermCount;
    protected String ngramLogFile;

    // -------------------------------
    // subcomponents
    // --------------------------------
    private BinaryLoader loader;
    private PrintWriter logFile;

    // -------------------------------
    // Working data
    // --------------------------------
    private Map<Word, UnigramProbability> unigramIDMap;
    private Map<WordSequence, NGramBuffer>[] loadedNGramBuffers;
    private LRUCache<WordSequence, ProbDepth> ngramDepthCache;
    private Map<Long, Float> bigramSmearMap;

    private NGramBuffer[] loadedBigramBuffers;
    private UnigramProbability[] unigrams;
    private int[][] ngramSegmentTable;
    private float[][] ngramProbTable;
    private float[][] ngramBackoffTable;
    private float[] unigramSmearTerm;
    
    public LargeNGramModel( String format, URL location, String ngramLogFile,
                              int maxNGramCacheSize, boolean clearCacheAfterUtterance, 
                              int maxDepth,  LogMath logMath, Dictionary dictionary,
                              boolean applyLanguageWeightAndWip, float languageWeight,
                              double wip, float unigramWeight, boolean fullSmear          
                              ) {
        logger = Logger.getLogger(getClass().getName());
        this.format = format;
        this.location = location;
        this.ngramLogFile = ngramLogFile;
        this.ngramCacheSize = maxNGramCacheSize;
        this.clearCacheAfterUtterance = clearCacheAfterUtterance;
        this.maxDepth = maxDepth;
        this.logMath = logMath;
        this.dictionary = dictionary;
        this.applyLanguageWeightAndWip = applyLanguageWeightAndWip;
        this.languageWeight = languageWeight;
        this.wip = wip;
        this.unigramWeight = unigramWeight;
        this.fullSmear = fullSmear;
    }

    public LargeNGramModel() {

    }


    /*
    * (non-Javadoc)
    *
    * @see edu.cmu.sphinx.util.props.Configurable#newProperties(edu.cmu.sphinx.util.props.PropertySheet)
    */
    @Override
    public void newProperties(PropertySheet ps) throws PropertyException {
        logger = ps.getLogger();
        location = ConfigurationManagerUtils.getResource(PROP_LOCATION, ps);
        ngramLogFile = ps.getString(PROP_QUERY_LOG_FILE);
        ngramCacheSize = ps.getInt(PROP_NGRAM_CACHE_SIZE);
        clearCacheAfterUtterance = ps.getBoolean(PROP_CLEAR_CACHES_AFTER_UTTERANCE);
        maxDepth = ps.getInt(LanguageModel.PROP_MAX_DEPTH);
        logMath = (LogMath) ps.getComponent(PROP_LOG_MATH);
        dictionary = (Dictionary) ps.getComponent(PROP_DICTIONARY);
        applyLanguageWeightAndWip = ps.getBoolean(PROP_APPLY_LANGUAGE_WEIGHT_AND_WIP);
        languageWeight = ps.getFloat(PROP_LANGUAGE_WEIGHT);
        wip = ps.getDouble(PROP_WORD_INSERTION_PROBABILITY);
        unigramWeight = ps.getFloat(PROP_UNIGRAM_WEIGHT);
        fullSmear = ps.getBoolean(PROP_FULL_SMEAR);
    }    
    
    /*
    * (non-Javadoc)
    *
    * @see edu.cmu.sphinx.linguist.language.ngram.LanguageModel#allocate()
    */
    @Override
    @SuppressWarnings("unchecked")
    public void allocate() throws IOException {
        TimerPool.getTimer(this, "Load LM").start();
       
        logger.info("Loading n-gram language model from: " + location);
       
        // create the log file if specified
        if (ngramLogFile != null)
            logFile = new PrintWriter(new FileOutputStream(ngramLogFile));
        
        if (location.getProtocol() == null || location.getProtocol().equals("file")) {
            loader = new BinaryLoader(new File(location.getFile()), format, applyLanguageWeightAndWip,
                                      logMath, languageWeight, wip, unigramWeight);
        } else {
            loader = new BinaryStreamLoader(location, format, applyLanguageWeightAndWip,
                    logMath, languageWeight, wip, unigramWeight);            
        }
                
        unigramIDMap = new HashMap<Word, UnigramProbability>();
        unigrams = loader.getUnigrams();
        loadedNGramBuffers = new Map[loader.getMaxDepth()];
        ngramProbTable = new float[loader.getMaxDepth()][];
        ngramBackoffTable = new float[loader.getMaxDepth()][];
        ngramSegmentTable = new int[loader.getMaxDepth()][];

		for (int i = 1; i <= loader.getMaxDepth(); i++) {
			loadedNGramBuffers[i - 1] = new HashMap<WordSequence, NGramBuffer>();

			if (i >= 2)
				ngramProbTable[i - 1] = loader.getNGramProbabilities(i);

			if (i > 2) {
				ngramBackoffTable[i - 1] = loader.getNGramBackoffWeights(i);
				ngramSegmentTable[i - 1] = loader.getNGramSegments(i);
			}
		}
        
        ngramDepthCache = new LRUCache<WordSequence, ProbDepth>(ngramCacheSize);
        if (dictionary != null)
        	buildUnigramIDMap(dictionary);
        else 
        	buildUnigramIDMap();
        loadedBigramBuffers = new NGramBuffer[unigrams.length];

        if (maxDepth <= 0 || maxDepth > loader.getMaxDepth())
            maxDepth = loader.getMaxDepth();

        for (int i = 1; i <= loader.getMaxDepth(); i++)
        	logger.info(Integer.toString(i) + "-grams: " + loader.getNumberNGrams(i));

        if (fullSmear) {
            System.out.println("Full Smear");
            try {
                System.out.println("... Reading ...");
                readSmearInfo("smear.dat");
                System.out.println("... Done ");
            } catch (IOException e) {
                System.out.println("... " + e);
                System.out.println("... Calculating");
                buildSmearInfo();
                System.out.println("... Writing");
                // writeSmearInfo("smear.dat");
                System.out.println("... Done");
            }
        }
        
        TimerPool.getTimer(this,"Load LM").stop();
    }

    /*
    * (non-Javadoc)
    *
    * @see edu.cmu.sphinx.linguist.language.ngram.LanguageModel#deallocate()
    */
    @Override
    public void deallocate() throws IOException {
        loader.deallocate();
    }


    /** Builds the map from unigram to unigramID. Also finds the startWordID and endWordID.
     * @param dictionary
     * */
    private void buildUnigramIDMap(Dictionary dictionary) {
        int missingWords = 0;
        String[] words = loader.getWords();
        for (int i = 0; i < words.length; i++) {
            Word word = dictionary.getWord(words[i]);

            if (word == null) {
                logger.warning("The dictionary is missing a phonetic transcription for the word '" + words[i] + "'");
                missingWords++;
            }
            
            unigramIDMap.put(word, unigrams[i]);
            
            if (logger.isLoggable(Level.FINE))
                logger.fine("Word: " + word);
        }

        if (missingWords > 0)
            logger.warning("Dictionary is missing " + missingWords + " words that are contained in the language model.");
    }
    
    private void buildUnigramIDMap() {
    	String[] words = loader.getWords();
    	for (int i = 0; i < words.length; i++) {
    		Word word = new Word(words[i], null, false);
    		
    		unigramIDMap.put(word, unigrams[i]);
    	}
    }


    /** Called before a recognition */
    @Override
    public void start() {
        if (logFile != null)
            logFile.println("<START_UTT>");
    }


    /** Called after a recognition */
    @Override
    public void stop() {
        clearCache();
        
        if (logFile != null) {
            logFile.println("<END_UTT>");
            logFile.flush();
        }
    }


    /** Clears the various N-gram caches. */
    private void clearCache() {
        for (int i = 0; i < loadedBigramBuffers.length; i++) {
            NGramBuffer buffer = loadedBigramBuffers[i];
            
            if (buffer != null) {
                if (!buffer.getUsed())
                    loadedBigramBuffers[i] = null; // free the BigramBuffer
                else
                    buffer.setUsed(false);
            }
        }

        
        loadedBigramBuffers = new NGramBuffer[unigrams.length];        
        for (int i = 2; i <= loader.getMaxDepth(); i++) {
            loadedNGramBuffers[i - 1] = new HashMap<WordSequence, NGramBuffer>();
        }
        logger.info("LM Cache Size: " + ngramDepthCache.size() + " Hits: " + ngramHits + " Misses: " + ngramMisses);
    	if (clearCacheAfterUtterance) {
    		ngramDepthCache = new LRUCache<WordSequence, ProbDepth>(ngramCacheSize);
    	}
    }

    /**
     * Returns predicted probability and depth. Uses caching for 
     * high order ngrams.
     * 
     * @param wordSequence sequence to get the probability
     */
    public ProbDepth getProbDepth(WordSequence wordSequence) {
        int numberWords = wordSequence.size();
        ProbDepth probDepth = null;

        if (numberWords > maxDepth) {
            throw new Error("Unsupported NGram: " + wordSequence.size());
        }

        if (numberWords == maxDepth) {
            probDepth = ngramDepthCache.get(wordSequence);

            if (probDepth != null) {
                ngramHits++;
                return probDepth;
            }
            ngramMisses++;
        }

        probDepth = getNGramProbDepth(wordSequence);

        if (numberWords == maxDepth)
            ngramDepthCache.put(wordSequence, probDepth);

        if (logFile != null && probDepth != null)
            logFile.println(wordSequence.toString().replace("][", " ") + " : " + Float.toString(probDepth.probability) + " : "
                    + probDepth.depth);

        return probDepth;
    }

    
    private ProbDepth getNGramProbDepth(WordSequence wordSequence) {
        int numberWords = wordSequence.size();
        Word firstWord = wordSequence.getWord(0);

        if (loader.getNumberNGrams(numberWords) == 0 || !hasUnigram(firstWord))
            return getNGramProbDepth(wordSequence.getNewest());

        if (numberWords < 2) {
            return getUnigramProbDepth(wordSequence);
        }

        NGramProbability nGProbability = findNGram(wordSequence);

        if (nGProbability != null) {
            float probability = ngramProbTable[numberWords - 1][nGProbability.getProbabilityID()];
            return new ProbDepth(probability, numberWords);
        }

        if (numberWords == 2) {
            UnigramProbability unigramProb = getUnigram(firstWord);
            UnigramProbability unigramProb1 = getUnigram(wordSequence.getWord(1));
            float probability = unigramProb.getLogBackoff() + unigramProb1.getLogProbability();
            return new ProbDepth(probability, 1);
        }

        NGramProbability nMinus1Gram = findNGram(wordSequence.getOldest());

        if (nMinus1Gram != null) {
            ProbDepth result1 = getProbDepth(wordSequence.getNewest());
            float probability = ngramBackoffTable[numberWords - 1][nMinus1Gram.getBackoffID()] + result1.probability;
            return new ProbDepth(probability, result1.depth);
        }

        return getProbDepth(wordSequence.getNewest());
    }

    /**
     * Gets the ngram probability of the word sequence represented by the word
     * list
     * 
     * @param wordSequence
     *            the word sequence
     * @return the probability of the word sequence. Probability is in logMath
     *         log base
     */
    @Override
    public float getProbability(WordSequence wordSequence) {
        ProbDepth probDepth = getProbDepth(wordSequence);
        return probDepth.probability;
    }

    
    /**
     * Finds or loads the NGram probability of the given NGram.
     *
     * @param wordSequence the NGram to load
     * @return a NGramProbability of the given NGram
     */
    private NGramProbability findNGram(WordSequence wordSequence) {
    	int numberWords = wordSequence.size();
        NGramProbability nGram = null; 
        
        WordSequence oldest = wordSequence.getOldest();
        NGramBuffer nGramBuffer = loadedNGramBuffers[numberWords - 1].get(oldest);
        if (nGramBuffer == null) {
        	nGramBuffer = getNGramBuffer(oldest);
            if (nGramBuffer != null)
                loadedNGramBuffers[numberWords - 1].put(oldest, nGramBuffer);
        }

        if (nGramBuffer != null) {
            int nthWordID = getWordID(wordSequence.getWord(numberWords - 1));
            nGram = nGramBuffer.findNGram(nthWordID);
        }

        return nGram;
    }

    /**
     * Tells if the model is 16 or 32 bits.
     *
     * @return true if 32 bits, false otherwise
     */
    private boolean is32bits() {
        if (loader.getBytesPerField() == 4)
            return true;        
        return false;
    }
    
    /**
     * Loads into a buffer all the NGram followers of the given N-1Gram.
     * 
     * @param ws the N-1Gram to find followers
     * 
     * @return a NGramBuffer of all the NGram followers of the given sequence
     */
    private NGramBuffer loadNGramBuffer(WordSequence ws) { 
    	int firstWordID = getWordID(ws.getWord(0));
    	int firstCurrentNGramEntry = 0;
    	int numberNGrams = 0;
    	int size = 0;
		long position = 0;
		int orderBuffer = ws.size() + 1;
		NGramBuffer currentBuffer = null;
		NGramBuffer nMinus1Buffer = null;

		firstCurrentNGramEntry = unigrams[firstWordID].getFirstBigramEntry(); 
		numberNGrams = getNumberBigramFollowers(firstWordID) + 1;
		
		if (numberNGrams == 1) // 1 means that there is no bigram starting with firstWordID
			return null;
        
		if (orderBuffer == 2)  {
			size = numberNGrams * ((loader.getMaxDepth() == orderBuffer) ? BYTES_PER_NMAXGRAM : BYTES_PER_NGRAM) * loader.getBytesPerField();
			position = (long) (loader.getNGramOffset(orderBuffer) + (firstCurrentNGramEntry * ((loader.getMaxDepth() == orderBuffer) ? BYTES_PER_NMAXGRAM : BYTES_PER_NGRAM) * loader.getBytesPerField()));
		} else { // only for ws.size() >= 2
			int lastWordId = getWordID(ws.getWord(ws.size() - 1));
			nMinus1Buffer = getNGramBuffer(ws.getOldest());
			int index = nMinus1Buffer.findNGramIndex(lastWordId);

			if (index == -1)
				return null;

			int firstNMinus1GramEntry = nMinus1Buffer.getFirstNGramEntry();
			firstCurrentNGramEntry = getFirstNGramEntry(nMinus1Buffer.getNGramProbability(index), firstNMinus1GramEntry, orderBuffer);
			int firstNextNGramEntry = getFirstNGramEntry(nMinus1Buffer.getNGramProbability(index + 1), firstNMinus1GramEntry, orderBuffer);
			numberNGrams = firstNextNGramEntry - firstCurrentNGramEntry;
			
			if (numberNGrams == 0)
				return null;
			
			if (loader.getMaxDepth() != orderBuffer) 
				numberNGrams++;

			size = numberNGrams * ((loader.getMaxDepth() == orderBuffer) ? BYTES_PER_NMAXGRAM : BYTES_PER_NGRAM) * loader.getBytesPerField();
			position = (long) loader.getNGramOffset(orderBuffer) + (long) firstCurrentNGramEntry * (long) ((loader.getMaxDepth() == orderBuffer) ? BYTES_PER_NMAXGRAM : BYTES_PER_NGRAM) * (long) loader.getBytesPerField();
		}

        try {
            byte[] buffer = loader.loadBuffer(position, size);

            if (loader.getMaxDepth() == orderBuffer) {
                currentBuffer = new NMaxGramBuffer(buffer, numberNGrams, loader.getBigEndian(), is32bits(), orderBuffer,
                        firstCurrentNGramEntry);
            } else {
                currentBuffer = new NGramBuffer(buffer, numberNGrams, loader.getBigEndian(), is32bits(), orderBuffer,
                        firstCurrentNGramEntry);
            }
        }
		catch (IOException ioe) {
			ioe.printStackTrace();
			throw new Error("Error loading " + orderBuffer + "-Grams.");
		}

		return currentBuffer;
    }

    
    /**
     * Returns the NGrams of the given word sequence
     *
     * @param wordSequence the word sequence 
     * 					   from which to get the buffer
     * @return the NGramBuffer of the word sequence
     */
    private NGramBuffer getNGramBuffer(WordSequence wordSequence) {
    	NGramBuffer nGramBuffer = null;
    	int order = wordSequence.size();
    	
    	if (order > 1)
    		nGramBuffer = loadedNGramBuffers[order - 1].get(wordSequence); // better when using containsKey

    	if (nGramBuffer == null) {
    		nGramBuffer = loadNGramBuffer(wordSequence);

    		if (nGramBuffer != null) 
    			loadedNGramBuffers[order - 1].put(wordSequence, nGramBuffer); // optimizable by adding an 'empty' nGramBuffer
    	}

    	return nGramBuffer;
    }
    
    
    /**
     * Returns the index of the first NGram entry of the given N-1Gram
     *
     * @param nMinus1Gram            the N-1Gram which first NGram entry we're looking for
     * @param firstNMinus1GramEntry  the index of the first N-1Gram entry of the N-1Gram in question
     * @param n						 the order of the NGram
     * @return the index of the first NGram entry of the given N-1Gram
     */
    private int getFirstNGramEntry(NGramProbability nMinus1Gram, int firstNMinus1GramEntry, int n) {
        int firstNGramEntry = ngramSegmentTable[n - 1][(firstNMinus1GramEntry + nMinus1Gram.getWhichFollower()) 
                                                  >> loader.getLogNGramSegmentSize()] 
                                                  + nMinus1Gram.getFirstNPlus1GramEntry();
        
        return firstNGramEntry;
    }
    
    
    /**
     * Returns the unigram probability of the given unigram.
     *
     * @param wordSequence the unigram word sequence
     * @return the unigram probability
     */
    private ProbDepth getUnigramProbDepth(WordSequence wordSequence) {
        Word unigram = wordSequence.getWord(0);
        UnigramProbability unigramProb = getUnigram(unigram);
        
        if (unigramProb == null)
            throw new Error("Unigram not in LM: " + unigram);
        
        return new ProbDepth(unigramProb.getLogProbability(), 1);
    }
	
	
    /**
     * Returns its UnigramProbability if this language model has the given unigram.
     *
     * @param unigram the unigram to find
     * @return the UnigramProbability, or null if this language model does not have the unigram
     */
    private UnigramProbability getUnigram(Word unigram) {
        return unigramIDMap.get(unigram);
    }


    /**
     * Returns true if this language model has the given unigram.
     *
     * @param unigram the unigram to find
     * @return true if this LM has this unigram, false otherwise
     */
    private boolean hasUnigram(Word unigram) {
        return (unigramIDMap.get(unigram) != null);
    }


    /**
     * Returns the ID of the given word.
     *
     * @param word the word to find the ID
     * @return the ID of the word
     */
    public final int getWordID(Word word) {
        UnigramProbability probability = getUnigram(word);
        
        if (probability == null)
            throw new IllegalArgumentException("No word ID: " + word);
        else
            return probability.getWordID();
    }
    
    /**
     * Returns true if the language model contains the given word
     * 
     * @param w
     * @return
     */
    public boolean hasWord(Word w) {
       return (unigramIDMap.get(new Word(w.toString(), null, false)) != null);
    }



    /**
     * Gets the smear term for the given wordSequence
     *
     * @param wordSequence the word sequence
     * @return the smear term associated with this word sequence
     */
    public float getSmearOld(WordSequence wordSequence) {
        float smearTerm = 0.0f;
        
        if (fullSmear) {
            int length = wordSequence.size();
            
            if (length > 0) {
                int wordID = getWordID(wordSequence.getWord(length - 1));
                smearTerm = unigramSmearTerm[wordID];
            }
        }
        
        if (fullSmear && logger.isLoggable(Level.FINE))
            logger.fine("SmearTerm: " + smearTerm);

        return smearTerm;
    }

    int smearCount;
    int smearBigramHit;

    @Override
    public float getSmear(WordSequence wordSequence) {
        float smearTerm = 0.0f;
        
        if (fullSmear) {
            smearCount++;
            int length = wordSequence.size();
            
            if (length == 1) {
                int wordID = getWordID(wordSequence.getWord(0));
                smearTerm = unigramSmearTerm[wordID];
            } else if (length >= 2) {
                int size = wordSequence.size();
                int wordID1 = getWordID(wordSequence.getWord(size - 2));
                int wordID2 = getWordID(wordSequence.getWord(size - 1));
                Float st = getSmearTerm(wordID1, wordID2);
                
                if (st == null)
                    smearTerm = unigramSmearTerm[wordID2];
                else {
                    smearTerm = st;
                    smearBigramHit++;
                }
            }

            if (smearCount % 100000 == 0)
                System.out.println("Smear hit: " + smearBigramHit + " tot: " + smearCount);
        }
        
        if (fullSmear && logger.isLoggable(Level.FINE))
            logger.fine("SmearTerm: " + smearTerm);

        return smearTerm;
    }


    /**
     * Returns the number of bigram followers of a word.
     *
     * @param wordID the ID of the word
     * @return the number of bigram followers
     */
    private int getNumberBigramFollowers(int wordID) {
        if (wordID == unigrams.length - 1)
            return 0;
        else
            return unigrams[wordID + 1].getFirstBigramEntry() - unigrams[wordID].getFirstBigramEntry();
    }


    /**
     * Returns the maximum depth of the language model
     *
     * @return the maximum depth of the language model
     */
    @Override
    public int getMaxDepth() {
        return maxDepth;
    }


    /**
     * Returns the set of words in the language model. The set is unmodifiable.
     *
     * @return the unmodifiable set of words
     */
    @Override
    public Set<String> getVocabulary() {
        Set<String> vocabulary = new HashSet<String>(Arrays.asList(loader.getWords()));
        return Collections.unmodifiableSet(vocabulary);
    }

    /**
     * Returns the number of times when a NGram is queried, 
     * but there is no such NGram in the LM 
     * (in which case it uses the backoff probabilities).
     *
     * @return the number of NGram misses
     */
    public int getNGramMisses() {
        return ngramMisses;
    }


    /**
     * Returns the number of NGram hits.
     *
     * @return the number of NGram hits
     */
    public int getNGramHits() {
        return ngramHits;
    }

    
	/**
	 * Returns the bigrams of the given word
	 * 
	 * @param firstWordID
	 *                the ID of the word
	 * 
	 * @return the bigrams of the word
	 */
	private NGramBuffer getBigramBuffer(int firstWordID) {
		Word[] wd =  new Word[1];
		wd[0] = dictionary.getWord(loader.getWords()[firstWordID]);
		WordSequence ws = new WordSequence(wd);

		return loadNGramBuffer(ws);
	}

	
	/**
	 * Loads into a buffer all the trigram followers of the given bigram.
	 * 
	 * @param firstWordID
	 *                the ID of the first word
	 * @param secondWordID
	 *                the ID of the second word
	 * 
	 * @return a TrigramBuffer of all the trigram followers of the given two
	 *         words
	 */
	private NGramBuffer loadTrigramBuffer(int firstWordID, int secondWordID) {
		Word[] wd =  new Word[2];
		wd[0] = dictionary.getWord(loader.getWords()[firstWordID]);
		wd[1] = dictionary.getWord(loader.getWords()[secondWordID]);
		WordSequence ws = new WordSequence(wd);

		return loadNGramBuffer(ws);
	}
	
	
    private void buildSmearInfo() throws IOException {
        double S0 = 0;
        double R0 = 0;

        bigramSmearMap = new HashMap<Long, Float>();

        double[] ugNumerator = new double[unigrams.length];
        double[] ugDenominator = new double[unigrams.length];
        double[] ugAvgLogProb = new double[unigrams.length];

        unigramSmearTerm = new float[unigrams.length];

        for (UnigramProbability unigram : unigrams) {
            float logp = unigram.getLogProbability();
            double p = logMath.logToLinear(logp);
            S0 += p * logp;
            R0 += p * logp * logp;
        }

        System.out.println("R0 S0 " + R0 + ' ' + S0);

        for (int i = 0; i < loadedBigramBuffers.length; i++) {
            NGramBuffer bigram = getBigramBuffer(i);
            
            if (bigram == null) {
                unigramSmearTerm[i] = LogMath.getLogOne();
                continue;
            }

            ugNumerator[i] = 0.0;
            ugDenominator[i] = 0.0;
            ugAvgLogProb[i] = 0.0;

            float logugbackoff = unigrams[i].getLogBackoff();
            double ugbackoff = logMath.logToLinear(logugbackoff);

            for (int j = 0; j < bigram.getNumberNGrams(); j++) {
                int wordID = bigram.getWordID(j);
                NGramProbability bgProb = bigram.getNGramProbability(j);

                float logugprob = unigrams[wordID].getLogProbability();
                float logbgprob = ngramProbTable[1][bgProb.getProbabilityID()];

                double ugprob = logMath.logToLinear(logugprob);
                double bgprob = logMath.logToLinear(logbgprob);

                double backoffbgprob = ugbackoff * ugprob;
                double logbackoffbgprob = logMath.linearToLog(backoffbgprob);

                ugNumerator[i] += (bgprob * logbgprob
                        - backoffbgprob * logbackoffbgprob) * logugprob;

                ugDenominator[i] += (bgprob - backoffbgprob) * logugprob;
                // dumpProbs(ugNumerator, ugDenominator, i, j, logugprob,
				//		logbgprob, ugprob, bgprob, backoffbgprob,
				//		logbackoffbgprob);
            }
            
            ugNumerator[i] += ugbackoff * (logugbackoff * S0 + R0);
            ugAvgLogProb[i] = ugDenominator[i] + ugbackoff * S0;
            ugDenominator[i] += ugbackoff * R0;

            // System.out.println("n/d " + ugNumerator[i] + " " +
            //                     ugDenominator[i]);

            unigramSmearTerm[i] = (float) (ugNumerator[i] / ugDenominator[i]);
            /// unigramSmearTerm[i] = 
            //   logMath.linearToLog(ugNumerator[i] / ugDenominator[i]);
            //  System.out.println("ugs " + unigramSmearTerm[i]);
        }

        for (int i = 0; i < loadedBigramBuffers.length; i++) {
            System.out.println("Processed " + i
                    + " of " + loadedBigramBuffers.length);
            NGramBuffer bigram = getBigramBuffer(i);
            
            if (bigram == null)
                continue;

            for (int j = 0; j < bigram.getNumberNGrams(); j++) {
                float smearTerm;
                NGramProbability bgProb = bigram.getNGramProbability(j);
                float logbgbackoff = ngramBackoffTable[2][bgProb.getBackoffID()];
                double bgbackoff = logMath.logToLinear(logbgbackoff);
                int k = bigram.getWordID(j);
                NGramBuffer trigram = loadTrigramBuffer(i, k);

                if (trigram == null)
                    smearTerm = unigramSmearTerm[k];
                else {
                    double bg_numerator = 0;
                    double bg_denominator = 0;
                    for (int l = 0; l < trigram.getNumberNGrams(); l++) {
                        int m = trigram.getWordID(l);
                        float logtgprob
                                = ngramProbTable[2][trigram.getProbabilityID(l)];
                        double tgprob = logMath.logToLinear(logtgprob);
                        float logbgprob = getBigramProb(k, m);
                        double bgprob = logMath.logToLinear(logbgprob);
                        float logugprob = unigrams[m].getLogProbability();
                        double backofftgprob = bgbackoff * bgprob;
                        double logbackofftgprob
                                = logMath.linearToLog(backofftgprob);

                        bg_numerator += (tgprob * logtgprob
                                - backofftgprob * logbackofftgprob) * logugprob;

                        bg_denominator += (tgprob - backofftgprob)
                                * logugprob * logugprob;
                    }
                    
                    bg_numerator += bgbackoff * (logbgbackoff * ugAvgLogProb[k] - ugNumerator[k]);
                    bg_denominator += bgbackoff * ugDenominator[k];
                    // bigram.ugsmear = bg_numerator / bg_denominator;
                    smearTerm = (float) (bg_numerator / bg_denominator);
                    smearTermCount++;
                }
                
                putSmearTerm(i, k, smearTerm);
            }
        }
        
        System.out.println("Smear count is " + smearTermCount);
    }

	@SuppressWarnings("unused")
	private void dumpProbs(double[] ugNumerator, double[] ugDenominator, int i,
			int j, float logugprob, float logbgprob, double ugprob,
			double bgprob, double backoffbgprob, double logbackoffbgprob) {

		System.out.println("ubo " + ugprob + ' ' + bgprob + ' ' +
		            backoffbgprob);
		    System.out.println("logubo " + logugprob
		            + ' ' + logbgprob + ' ' + logbackoffbgprob);
		    System.out.println("n/d " + j + ' '
		            + ugNumerator[i] + ' ' + ugDenominator[i]);

		    
		    System.out.print(ugprob + " " + bgprob + ' '
		            + backoffbgprob);
		    System.out.print(" " + logugprob + ' '
		            + logbgprob + ' ' + logbackoffbgprob);
		    System.out.println("  " + ugNumerator[i]
		            + ' ' + ugDenominator[i]);
	}


    /**
     * Writes the smear info to the given file
     *
     * @param filename the file to write the smear info to
     * @throws IOException if an error occurs on write
     */
    @SuppressWarnings("unused")
    private void writeSmearInfo(String filename) throws IOException {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));
        out.writeInt(SMEAR_MAGIC);
        System.out.println("writing " + unigrams.length);
        out.writeInt(unigrams.length);

        for (int i = 0; i < unigrams.length; i++)
            out.writeFloat(unigramSmearTerm[i]);

        for (int i = 0; i < unigrams.length; i++) {
            System.out.println("Writing " + i + " of " + unigrams.length);
            NGramBuffer bigram = getBigramBuffer(i);
            
            if (bigram == null) {
                out.writeInt(0);
                continue;
            }
            
            out.writeInt(bigram.getNumberNGrams());
            
            for (int j = 0; j < bigram.getNumberNGrams(); j++) {
                int k = bigram.getWordID(j);
                Float smearTerm = getSmearTerm(i, k);
                out.writeInt(k);
                out.writeFloat(smearTerm.floatValue());
            }
        }
        
        out.close();
    }


    /**
     * Reads the smear info from the given file
     *
     * @param filename where to read the smear info from
     * @throws IOException if an inconsistent file is found or on any general I/O error
     */
    private void readSmearInfo(String filename) throws IOException {
        DataInputStream in = new DataInputStream(new FileInputStream(filename));


        if (in.readInt() != SMEAR_MAGIC)
            throw new IOException("Bad smear format for " + filename);

        if (in.readInt() != unigrams.length)
            throw new IOException("Bad unigram length in " + filename);

        bigramSmearMap = new HashMap<Long, Float>();
        unigramSmearTerm = new float[unigrams.length];
        System.out.println("Reading " + unigrams.length);
        
        for (int i = 0; i < unigrams.length; i++)
            unigramSmearTerm[i] = in.readFloat();

        for (int i = 0; i < unigrams.length; i++) {
            System.out.println("Processed " + i
                    + " of " + loadedBigramBuffers.length);
            int numBigrams = in.readInt();
            NGramBuffer bigram = getBigramBuffer(i);
            
            if (bigram.getNumberNGrams() != numBigrams)
                throw new IOException("Bad ngrams for unigram " + i + " Found " + numBigrams + " expected " + bigram.getNumberNGrams());

            for (int j = 0; j < numBigrams; j++) {
                int k = bigram.getWordID(j);
                putSmearTerm(i, k, in.readFloat());
            }
        }
        
        in.close();
    }


    /**
     * Puts the smear term for the two words
     *
     * @param word1     the first word
     * @param word2     the second word
     * @param smearTerm the smear term
     */
    private void putSmearTerm(int word1, int word2, float smearTerm) {
        long bigramID = (((long) word1) << 32) | word2;
        bigramSmearMap.put(bigramID, smearTerm);
    }


    /**
     * Retrieves the smear term for the two words
     *
     * @param word1 the first word
     * @param word2 the second word
     * @return the smear term
     */
    private Float getSmearTerm(int word1, int word2) {
        long bigramID = (((long) word1) << 32) | word2;
        return bigramSmearMap.get(bigramID);
    }


    /**
     * Retrieves the bigram probability for the two given words
     *
     * @param word1 the first word of the bigram
     * @param word2 the second word of the bigram
     * @return the log probability
     */
    private float getBigramProb(int word1, int word2) {
        NGramBuffer bigram = getBigramBuffer(word1);
        NGramProbability bigramProbability = bigram.findNGram(word2);
        return ngramProbTable[1][bigramProbability.getProbabilityID()];
    }
    
}
