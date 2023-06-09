package com.bangla.keyboard.commons.digester3;

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


/**
 * <p>
 * <code>AbstractRuleImpl</code> provides basic services for <code>Rules</code> implementations. Extending this class
 * should make it easier to create a <code>Rules</code> implementation.
 * </p>
 * <p>
 * <code>AbstractRuleImpl</code> manages the <code>Digester</code> and <code>namespaceUri</code> properties. If the
 * subclass overrides {@link #registerRule} (rather than {@link #add}), then the <code>Digester</code> and
 * <code>namespaceURI</code> of the <code>Rule</code> will be set correctly before it is passed to
 * <code>registerRule</code>. The subclass can then perform whatever it needs to do to register the rule.
 * </p>
 * 
 * @since 1.5
 */
public abstract class AbstractRulesImpl
    implements Rules
{

    // ------------------------------------------------------------- Fields

    /** Digester using this <code>Rules</code> implementation */
    private Digester digester;

    /** Namespace uri to assoicate with subsequent <code>Rule</code>'s */
    private String namespaceURI;

    // ------------------------------------------------------------- Properties

    /**
     * {@inheritDoc}
     */
    public Digester getDigester()
    {
        return digester;
    }

    /**
     * {@inheritDoc}
     */
    public void setDigester( Digester digester )
    {
        this.digester = digester;
    }

    /**
     * {@inheritDoc}
     */
    public String getNamespaceURI()
    {
        return namespaceURI;
    }

    /**
     * {@inheritDoc}
     */
    public void setNamespaceURI( String namespaceURI )
    {
        this.namespaceURI = namespaceURI;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * {@inheritDoc}
     */
    public final void add(String pattern, Rule rule )
    {
        // set up rule
        if ( this.digester != null )
        {
            rule.setDigester( this.digester );
        }

        if ( this.namespaceURI != null )
        {
            rule.setNamespaceURI( this.namespaceURI );
        }

        registerRule( pattern, rule );
    }

    /**
     * Register rule at given pattern. The the Digester and namespaceURI properties of the given <code>Rule</code> can
     * be assumed to have been set properly before this method is called.
     * 
     * @param pattern Nesting pattern to be matched for this Rule
     * @param rule Rule instance to be registered
     */
    protected abstract void registerRule(String pattern, Rule rule );

}
