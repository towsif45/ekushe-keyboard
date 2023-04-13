package com.bangla.keyboard.commons.digester3.xmlrules;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.bangla.keyboard.commons.digester3.Rule;
import com.bangla.keyboard.commons.digester3.binder.SetPropertiesBuilder;

import org.xml.sax.Attributes;

/**
 *
 */
final class SetPropertiesIgnoreRule
    extends Rule
{

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin(String namespace, String name, Attributes attributes )
        throws Exception
    {
        String attributeName = attributes.getValue( "attr-name" );

        SetPropertiesBuilder builder = getDigester().peek();
        builder.ignoreAttribute( attributeName );
    }

}
