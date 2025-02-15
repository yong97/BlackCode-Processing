/*
 * Copyright (c) 2011, 2012 events-on-fire Team
 * 
 * This file is part of Events-On-Fire (http://code.google.com/p/events-on-fire), licensed under the terms of the MIT
 * License (MIT).
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.qscript.eventsonfire;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * An abstract implementation of an {@link EventHandlerStrategy} that scans a class for possible event handlers
 * 
 * @author Manfred HANTSCHEL
 */
public abstract class AbstractEventHandlerStrategy implements EventHandlerStrategy
{

    public AbstractEventHandlerStrategy()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void scan(Collection<EventHandlerInfo> infos, Class<?> type)
    {
        for (Method method : type.getMethods())
        {
            EventHandlerInfo eventHandlerInfo = createEventHandlerInfo(method);

            if (eventHandlerInfo != null)
            {
                infos.add(eventHandlerInfo);
            }
        }
    }

    /**
     * Creates an {@link EventHandlerInfo} if the method is capable of handling events.
     * 
     * @param method the method
     * @return the {@link EventHandlerInfo}, or null if method does not handle events
     */
    protected abstract EventHandlerInfo createEventHandlerInfo(Method method);

}
