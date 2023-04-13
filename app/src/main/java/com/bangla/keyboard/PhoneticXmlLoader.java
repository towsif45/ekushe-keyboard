/*
    =============================================================================
    *****************************************************************************
    The contents of this file are subject to the Mozilla Public License
    Version 1.1 (the "License"); you may not use this file except in
    compliance with the License. You may obtain a copy of the License at
    http://www.mozilla.org/MPL/

    Software distributed under the License is distributed on an "AS IS"
    basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
    License for the specific language governing rights and limitations
    under the License.

    The Original Code is JAvroPhonetic

    The Initial Developer of the Original Code is
    Rifat Nabi <to.rifat@gmail.com>

    Copyright (C) OmicronLab (http://www.omicronlab.com). All Rights Reserved.


    Contributor(s): ______________________________________.

    *****************************************************************************
    =============================================================================
*/

package com.bangla.keyboard;

import android.content.Context;
import android.content.res.AssetManager;

import com.bangla.keyboard.commons.digester3.Digester;
import com.bangla.keyboard.phonetic.Data;
import com.bangla.keyboard.phonetic.Match;
import com.bangla.keyboard.phonetic.Pattern;
import com.bangla.keyboard.phonetic.Rule;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.SAXParserFactory;

public class PhoneticXmlLoader implements PhoneticLoader {

    private URL url = null;
    InputStream in = null;

    public PhoneticXmlLoader(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            in = assetManager.open("phonetic.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(">>>>"+this.in);
    }

    public PhoneticXmlLoader(Context context, boolean flag) {
        AssetManager assetManager = context.getAssets();
        try {
            if(flag == true)
                in = assetManager.open("phonetic.xml");
            else
                in = assetManager.open("phonetic_bta.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(">>>>"+this.in);
    }

    public PhoneticXmlLoader(String path) throws MalformedURLException {
        this.url = new URL(path);
    }

    public Data getData() throws IOException, SAXException {


//        Digester digester = new Digester();
        final SAXParserFactory factory = SAXParserFactory.newInstance();

        Digester digester = new Digester() {
            @Override
            public SAXParserFactory getFactory() {
                System.out.println("using custom factory...");
                return factory;
            }
        };



        digester.setValidating(false);

        digester.addObjectCreate("data", Data.class);
        digester.addBeanPropertySetter("data/classes/vowel", "vowel");
        digester.addBeanPropertySetter("data/classes/consonant", "consonant");
        digester.addBeanPropertySetter("data/classes/punctuation", "punctuation");
        digester.addBeanPropertySetter("data/classes/casesensitive", "casesensitive");

        digester.addObjectCreate("data/patterns/pattern", Pattern.class);
        digester.addBeanPropertySetter("data/patterns/pattern/find", "find");
        digester.addBeanPropertySetter("data/patterns/pattern/replace", "replace");

        digester.addObjectCreate("data/patterns/pattern/rules/rule", Rule.class);
        digester.addBeanPropertySetter("data/patterns/pattern/rules/rule/replace", "replace");

        digester.addObjectCreate("data/patterns/pattern/rules/rule/find/match", Match.class);
        digester.addBeanPropertySetter("data/patterns/pattern/rules/rule/find/match", "value");
        digester.addSetProperties("data/patterns/pattern/rules/rule/find/match", "type", "type");
        digester.addSetProperties("data/patterns/pattern/rules/rule/find/match", "scope", "scope");

        digester.addSetNext("data/patterns/pattern/rules/rule/find/match", "addMatch");

        digester.addSetNext("data/patterns/pattern/rules/rule", "addRule");

        digester.addSetNext("data/patterns/pattern", "addPattern");

//        Data data = (Data) digester.parse("file:/E:/thesis new/android/MyApplication/app/src/main/res/xml/phonetic.xml");
        Data data = (Data) digester.parse(in);

        return data;
    }
}
