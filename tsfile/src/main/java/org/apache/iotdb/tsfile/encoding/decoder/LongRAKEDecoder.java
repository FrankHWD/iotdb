/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.tsfile.encoding.decoder;

import java.nio.ByteBuffer;

public class LongRAKEDecoder extends RAKEDecoder {

  @Override
  public long readLong(ByteBuffer buffer) {
    parseBuffer(buffer, 64);
    String subNumBuffer = numBuffer.substring(0, 64);
    this.numBuffer = "";
    if (subNumBuffer.charAt(0) == '0') {
      long r = Long.parseLong(subNumBuffer, 2);
      return r;
    } else {
      String tmpSubNumBuffer = "0";
      for (int i = 1; i < subNumBuffer.length(); i++) {
        if (subNumBuffer.charAt(i) == '1') tmpSubNumBuffer += "0";
        else tmpSubNumBuffer += "1";
      }
      long r = -Long.parseLong(tmpSubNumBuffer, 2) - 1;
      return r;
    }
  }
}