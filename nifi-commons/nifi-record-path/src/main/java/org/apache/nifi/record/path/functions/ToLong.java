/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.record.path.functions;


import org.apache.nifi.record.path.FieldValue;
import org.apache.nifi.record.path.RecordPathEvaluationContext;
import org.apache.nifi.record.path.StandardFieldValue;
import org.apache.nifi.record.path.paths.RecordPathSegment;
import org.apache.nifi.record.path.util.RecordPathUtils;
import org.apache.nifi.serialization.record.util.DataTypeUtils;

import java.util.Date;
import java.util.stream.Stream;

public class ToLong extends RecordPathSegment {

    private final RecordPathSegment recordPath;

    public ToLong(final RecordPathSegment recordPath, final boolean absolute) {
        super("toLong", null, absolute);
        this.recordPath = recordPath;
    }

    @Override
    public Stream<FieldValue> evaluate(RecordPathEvaluationContext context) {
        final Stream<FieldValue> fieldValues = recordPath.evaluate(context);
        return fieldValues.filter(fv -> fv.getValue() != null)
                .map(fv -> {

                    if (!(fv.getValue() instanceof String) && !(fv.getValue() instanceof Date) && !(fv.getValue() instanceof Number)) {
                        return fv;
                    }

                    final Long longValue;
                    try {
                        longValue = DataTypeUtils.toLong(fv.getValue(), fv.getField().getFieldName());
                    } catch (final Exception e) {
                        return fv;
                    }

                    if (longValue == null) {
                        return fv;
                    }

                    return new StandardFieldValue(longValue, fv.getField(), fv.getParent().orElse(null));
                });
    }

}