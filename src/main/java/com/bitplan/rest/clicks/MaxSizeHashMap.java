/**
 * Copyright (c) 2016-2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.simplerest
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.rest.clicks;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * https://stackoverflow.com/a/5601377/1497139
 *
 * @param <K>
 * @param <V>
 */
public class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {
  /**
   * 
   */
  private static final long serialVersionUID = -2021803868206384663L;
  private final int maxSize;

  public MaxSizeHashMap(int maxSize) {
      this.maxSize = maxSize;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
      return size() > maxSize;
  }
}
