/*
 * Copyright (C) 2017 Dmytro Shkil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shkil.android.util.event;

import android.util.Log;

public abstract class AbstractEvent implements IEvent {

    private static int defaultLogLevel = Log.VERBOSE;

    public static void setDefaultLogLevel(int level) {
        AbstractEvent.defaultLogLevel = level;
    }

    @Override
    public int getLogLevel() {
        return defaultLogLevel;
    }

}
