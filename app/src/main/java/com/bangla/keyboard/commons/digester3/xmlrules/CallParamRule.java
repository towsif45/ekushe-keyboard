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

import com.bangla.keyboard.commons.digester3.binder.CallParamBuilder;
import com.bangla.keyboard.commons.digester3.binder.LinkedRuleBuilder;
import com.bangla.keyboard.commons.digester3.binder.RulesBinder;

import org.xml.sax.Attributes;

/**
 * 
 */
final class CallParamRule
    extends AbstractXmlRule
{

    public CallParamRule(RulesBinder targetRulesBinder, PatternStack patternStack )
    {
        super( targetRulesBinder, patternStack );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void bindRule(LinkedRuleBuilder linkedRuleBuilder, Attributes attributes )
        throws Exception
    {
        int paramIndex = Integer.parseInt( attributes.getValue( "paramnumber" ) );
        CallParamBuilder builder = linkedRuleBuilder.callParam().ofIndex( paramIndex );

        String attributeName = attributes.getValue( "attrname" );
        String fromStack = attributes.getValue( "from-stack" );
        String stackIndex = attributes.getValue( "stack-index" );

        if ( attributeName == null )
        {
            if ( stackIndex != null )
            {
                builder.withStackIndex( Integer.parseInt( stackIndex ) );
            }
            else if ( fromStack != null )
            {
                builder.fromStack( Boolean.valueOf( fromStack ).booleanValue() );
            }
        }
        else
        {
            if ( fromStack == null )
            {
                builder.fromAttribute( attributeName );
            }
            else
            {
                // specifying both from-stack and attribute name is not allowed
                throw new RuntimeException( "Attributes from-stack and attrname cannot both be present." );
            }
        }
    }

}
