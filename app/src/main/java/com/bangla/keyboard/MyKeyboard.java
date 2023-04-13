package com.bangla.keyboard;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import androidx.core.content.FileProvider;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by khyo on 10-Nov-16.
 */

public class MyKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {


    private static final String AUTHORITY = "com.example.android.commitcontent.ime.inputcontent";
    private static final String TAG = "ImageKeyboard";
    private static final String MIME_TYPE_PNG = "image/png";

    private File mPngFile;

    Context context = this;
    private PopupWindow popup;
    private EditorInfo attribute;
    private CustomKeyboardView kv;
    private CustomKeyboard mQwertyKeyboard;
    private CustomKeyboard mQwertyEngKeyboard;
    private CustomKeyboard mProvatKeyboard;
    private CustomKeyboard mProvatKeyboardShift;
    private CustomKeyboard mSymbolsKeyboard;
    private CustomKeyboard mSymbolsBngKeyboard;
    private CustomKeyboard mSymbolsShiftedKeyboard;
    public CustomKeyboard mCurKeyboard;
    private CustomKeyboard mEmojiKeyboard;
    private CustomKeyboard mEmoji2Keyboard;
    private CustomKeyboard mEmoji3Keyboard;
    private CustomKeyboard mEmoji4Keyboard;
    private CustomKeyboard mEmoji5Keyboard;
    private boolean caps = false, oncePrinted = false;
    private InputMethodManager mInputMethodManager;
    private List<Keyboard.Key> keyList;


    private int onpress_code;

    public String[] keys = new String[5000];
    public String printChar = "", letterCount="";
    public String suggestionWord = "", suggIcWord="";
    public String toBeDeleted = "";
    private String mWordSeparators;

    public int count=0, q=0, w=0, e=0, r=0, t=0, y=0, u=0, i=0, o=0, p=0, a=0, s=0, d=0, f=0, g=0, h=0, j=0, k=0, l=0, z=0, x=0, c=0, v=0, b=0, n=0, m=0;
    public  long wordrun=0, sumOfinarr = 0;
    public int[] inarr= new int[50];
    public static String[] allSuggestedWOrds = new String[1000];
    public static String[] allSuggestedWOrdsInEng = new String[1000];
    public static List<String> possibleNextWOrdsInBng = new ArrayList<>();
    public static List<String> possibleNextWOrdsInEng = new ArrayList<>();
    public static List<String> emojis = new ArrayList<>();
    public static List<String> empty = new ArrayList<>();
    public static int totalSuggestedWOrdsCount=0;
    private CandidateView mCandidateView;
    private InputConnection ic;
    public long cur_time_onpress, cur_time_onkey, cur_time_onrelease, word_duration, cur_time_gesture;
    Trie v_trie, e_trie;

    private String filename = "WrittenWords.txt";
    private String filename_eng = "WrittenWordsEng.txt";
    private String filepath = "NewFileStorage";
    File myExternalFile;
    public String myData = "";
    private File keyboardDataFile;
    private File keyboardDataFileEng;
    private long mLastShiftTime;
    private boolean mCapsLock;
    private boolean capsLocked = false;
    public boolean isNexWord=false;
    private StringBuilder mComposing = new StringBuilder();
    private StringBuilder mComposing_bn_ver = new StringBuilder();
    private boolean mCompletionOn;
    private boolean fromType;
    private int codeForEngQwertyKeyboard = -105;
    private CustomKeyboard lastKeyboard;
    private CustomKeyboard lastKeyboardSymbol;
    private boolean islastWordSwipe = false;
    private boolean laterCharofAKey = false;
    private int primaryKeyofOnPress;
    private int prevPrimaryCodeofCurPref=0;
    private int idxOfBengSugEndStList = 0;
    private int idxOfEndOfBengSugShow = 0;
    private String lastWrittenWordWithSwipe;
    private boolean isLastWordSwipdeAndAnSpaceTapped;
    private boolean isSuggestionTapped = false;
    private String nextWordShownAfterTapping;
    private String WordShownAfterTappingSuggestion="";
    private String last_word_3gram = "";
    private int change = 0;
    private boolean init_tree = true;
    private boolean sugg_flag = true;
    private boolean next_word_3gram = false;

    public Vibrator vib ;
    public AudioManager am;



    //Next Word Prediction
    public static ArrayList<String> al_comments = new ArrayList<String>();
    public static String documentsPath = "/media/ranit/soft & document/CodeOther/Java/AIDataSet/commentSeparation/positive_test_773.txt";
    public static List< HashMap< String, Integer > > list_of_words_mp = new ArrayList< HashMap< String, Integer > >();
    public static HashMap< String, Integer > mp1_word_id = new HashMap<>();
    private List<Integer> delCharCorIcLet = new ArrayList<>();

    //Next Word Prediction English
    public static ArrayList<String> al_comments_eng = new ArrayList<String>();
    public static List< HashMap< String, Integer > > list_of_words_mp_eng = new ArrayList< HashMap< String, Integer > >();
    public static HashMap< String, Integer > mp1_word_id_eng = new HashMap<>();
    //for trigram
    public static List< HashMap< String, Integer > > list_of_words_mp_trigram = new ArrayList< HashMap< String, Integer > >();
    public static HashMap< String, Integer > mp1_word_id_trigram = new HashMap<>();
    public EmojiPredictorLSTM emojiPredictorLSTM;
    public static List<String> emojilist=new ArrayList<String>();
    public static HashMap<String, Integer> vocabulary_for_emoji=new HashMap<String, Integer>();

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    // not used in our code
    @Override public void onCreate() {
//        System.out.println("<<                              onCreate Starts                                          >>");
        super.onCreate();
        mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);

        emojis.add(String.valueOf(Character.toChars(128513)));
        emojis.add(String.valueOf(Character.toChars(128514)));
        emojis.add(String.valueOf(Character.toChars(128515)));
        emojis.add(String.valueOf(Character.toChars(128542)));
        emojis.add(String.valueOf(Character.toChars(128525)));
        emojis.add(String.valueOf(Character.toChars(128526)));
        emojis.add(String.valueOf(Character.toChars(128529)));

        emojis.add(String.valueOf(Character.toChars(128545)));
        emojis.add(String.valueOf(Character.toChars(128540)));
        emojis.add(String.valueOf(Character.toChars(128536)));
        emojis.add(String.valueOf(Character.toChars(128541)));
        emojis.add(String.valueOf(Character.toChars(128169)));
        emojis.add(String.valueOf(Character.toChars(128535)));
        emojis.add(String.valueOf(Character.toChars(128536)));

        emojiPredictorLSTM=new EmojiPredictorLSTM(this);

        /*
         * this is for loading emoji, vocabulary for ml
         *
         * */

        InputStream inputStream=getResources().openRawResource(R.raw.decoded_emoji);

        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));

        try {
            String csvLine;
            while ((csvLine = reader.readLine())!=null)
            {
                String[] row=csvLine.split(",");
                emojilist.add(row[1]);
            }
        }catch (IOException ex)
        {
            throw  new RuntimeException(ex);
        } finally {
            try{
                inputStream.close();
            } catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }

        }
        System.out.println("tt 205 ");
        for(String s:emojilist)
        {
            System.out.println(s);
        }

        inputStream=getResources().openRawResource(R.raw.vocabulary);

        reader=new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine())!=null)
            {
                String[] row=csvLine.split(",");
                vocabulary_for_emoji.put(row[0], Integer.valueOf(row[1]));
            }
        }catch (IOException ex)
        {
            throw  new RuntimeException(ex);
        } finally {
            try{
                inputStream.close();
            } catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }

        }
        System.out.println("tt 233 ");
        for(String i:vocabulary_for_emoji.keySet())
        {
            System.out.println(i+" "+vocabulary_for_emoji.get(i));
        }




//        final File imagesDir = new File(getFilesDir(), "images");
//        imagesDir.mkdirs();
//        mPngFile = getFileForResource(this, R.drawable.app_logo, imagesDir, "image.png");

//
//        PhoneticParserBTA avro_bta = PhoneticParserBTA.getInstance();
//        avro_bta.setLoader(new PhoneticXmlLoaderBTA(context,false));
//        String bangla = avro_bta.parse("আমাদের দেশ");
//        System.out.println(bangla);

//        System.out.println("<<                              onCreateEnds                                                  >>");
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override public void onInitializeInterface() {
//        System.out.println(">>>onInitializeInterface starts >>>>>>>>");
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mQwertyKeyboard = new CustomKeyboard(this, R.xml.qwerty_bng);
        mQwertyEngKeyboard = new CustomKeyboard(this, R.xml.qwerty_eng);
        mSymbolsKeyboard = new CustomKeyboard(this, R.xml.symbols);
        mSymbolsBngKeyboard = new CustomKeyboard(this, R.xml.symbols_bng);
        mSymbolsShiftedKeyboard = new CustomKeyboard(this, R.xml.symbols_shift);
        mEmojiKeyboard = new CustomKeyboard(this,R.xml.emojis1);
        mEmoji2Keyboard = new CustomKeyboard(this,R.xml.emojis2);
        mEmoji3Keyboard = new CustomKeyboard(this,R.xml.emojis3);
        mEmoji4Keyboard = new CustomKeyboard(this,R.xml.emojis4);
        mEmoji5Keyboard = new CustomKeyboard(this,R.xml.emojis5);
        mProvatKeyboard = new CustomKeyboard(this,R.xml.provat_bng);
        mProvatKeyboardShift = new CustomKeyboard(this,R.xml.provat_shift);
//        System.out.println(">>>onInitializeInterface ends >>>>>>>>");
    }

    public void retrieveKeys() {
//        System.out.println("<<                              retrievekeys start                                               >>");
        keyList = kv.getKeyboard().getKeys();
        for (Keyboard.Key key : keyList) {
//            System.out.println("code " + key.label + " x " + key.x + " y "+key.y);
        }
        Log.d("retrieveKeys", "retrieveKeys");

//        System.out.println("<<                              retrievekeys end                                                 >>");
    }




    //Data Collection
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
    //Data Collection

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        System.out.println("<<                              onstartinputview starts                                              >>");
        super.onStartInputView(info, restarting);
        ic = getCurrentInputConnection();

//        SharedPreferences prefs = getSharedPreferences("test", MODE_PRIVATE);
//        int flag = prefs.getInt("theme",0);

//        System.out.println("flag >>>>>>>> " + flag +" change>> "+change);
//        if(flag != change){
//            change = flag;
//            setInputView(onCreateInputView());
//        }

//        String[] mimeTypes = EditorInfoCompat.getContentMimeTypes(info);
//
//        boolean gifSupported = false;
//        for (String mimeType : mimeTypes) {
//            if (ClipDescription.compareMimeTypes(mimeType, "image/png")) {
//                gifSupported = true;
//            }
//        }
//
//        if (gifSupported) {
//            System.out.println(">>>>>>>>>>>>>>>>");
//            System.out.println(">>>>>>>>>>>>>>>>");
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>Gif  Supported");
//            Toast toast= Toast.makeText(getApplicationContext(),
//                    "Supported", Toast.LENGTH_SHORT);
//            toast.show();
//        } else {
//            System.out.println(">>>>>>>>>>>>>>>>");
//            System.out.println(">>>>>>>>>>>>>>>>");
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>not Gif  Supported");
//            Toast toast= Toast.makeText(getApplicationContext(),
//                    "NOT supported", Toast.LENGTH_SHORT);
//            toast.show();
//        }

        setInputView(onCreateInputView());
        setLatinKeyboard(mCurKeyboard);
        kv.closing();
        retrieveKeys();

        isNexWord = false;
        sugg_flag = true;

//        mCandidateView.setSuggestions(empty, true, true);
        mCandidateView.setSuggestions(emojis, true, true);
        setCandidatesViewShown(true);

        Log.d("onStartInputView", "onStartInputView");
        System.out.println("<<                              onstartinputview ends                                               >>");
    }

//    private static File getFileForResource(Context context,int res,File outputDir, String filename) {
//        final File outputFile = new File(outputDir, filename);
//        final byte[] buffer = new byte[4096];
//        InputStream resourceReader = null;
//        try {
//            try {
//                resourceReader = context.getResources().openRawResource(res);
//                OutputStream dataWriter = null;
//                try {
//                    dataWriter = new FileOutputStream(outputFile);
//                    while (true) {
//                        final int numRead = resourceReader.read(buffer);
//                        if (numRead <= 0) {
//                            break;
//                        }
//                        dataWriter.write(buffer, 0, numRead);
//                    }
//                    return outputFile;
//                } finally {
//                    if (dataWriter != null) {
//                        dataWriter.flush();
//                        dataWriter.close();
//                    }
//                }
//            } finally {
//                if (resourceReader != null) {
//                    resourceReader.close();
//                }
//            }
//        } catch (IOException e) {
//            return null;
//        }
//    }
//
//
//
//    private void doCommitContent( String description,  String mimeType, File file) {
//        final EditorInfo editorInfo = getCurrentInputEditorInfo();
//
//        // Validate packageName again just in case.
////        if (!validatePackageName(editorInfo)) {
////            return;
////        }
//
//        final Uri contentUri = FileProvider.getUriForFile(this, AUTHORITY, file);
//
//        // As you as an IME author are most likely to have to implement your own content provider
//        // to support CommitContent API, it is important to have a clear spec about what
//        // applications are going to be allowed to access the content that your are going to share.
//        final int flag;
//        if (Build.VERSION.SDK_INT >= 25) {
//            // On API 25 and later devices, as an analogy of Intent.FLAG_GRANT_READ_URI_PERMISSION,
//            // you can specify InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION to give
//            // a temporary read access to the recipient application without exporting your content
//            // provider.
//            flag = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
//        } else {
//            // On API 24 and prior devices, we cannot rely on
//            // InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION. You as an IME author
//            // need to decide what access control is needed (or not needed) for content URIs that
//            // you are going to expose. This sample uses Context.grantUriPermission(), but you can
//            // implement your own mechanism that satisfies your own requirements.
//            flag = 0;
//            try {
//                // TODO: Use revokeUriPermission to revoke as needed.
//                grantUriPermission(
//                        editorInfo.packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            } catch (Exception e){
//                Log.e(TAG, "grantUriPermission failed packageName=" + editorInfo.packageName
//                        + " contentUri=" + contentUri, e);
//            }
//        }
//
//        final InputContentInfoCompat inputContentInfoCompat = new InputContentInfoCompat(
//                contentUri,
//                new ClipDescription(description, new String[]{mimeType}),
//                null /* linkUrl */);
//        InputConnectionCompat.commitContent(
//                getCurrentInputConnection(), getCurrentInputEditorInfo(), inputContentInfoCompat,
//                flag, null);
//    }

    @Override
    public View onCreateInputView() {

        /* Testing Trie importing from java netbeans */

        View custom = LayoutInflater.from(context)
                .inflate(R.layout.preview, new FrameLayout(context));
        popup = new PopupWindow(context);
        popup.setContentView(custom);

        Log.d("--------Trie------", "------Trie-------");
//        System.out.println("-------------------------------- Trie-----------------------------------------------------------" );
        System.out.println("<<                              oncreateinputview starts                                                 >>");

        if(init_tree) {
            v_trie = new Trie(context);
            e_trie = new Trie(context);

            e_trie.insert("algo");
            e_trie.insert("algea");
            e_trie.insert("also");
            e_trie.insert("tom");
            e_trie.insert("to");
            e_trie.insert("as");
            e_trie.insert("the");
            e_trie.insert("eat");
            e_trie.insert("boy");
            e_trie.insert("cat");
            e_trie.insert("dog");


//        v_trie.insert("amra"); v_trie.insert("ami"); v_trie.insert("amar"); v_trie.insert("amader"); v_trie.insert("valo");
//        v_trie.insert("to"); v_trie.insert("bangla"); v_trie.insert("bangladesh"); v_trie.insert("zuk`t");
            next_word_init();
            String data = "";

            StringBuffer sBuffer = new StringBuffer();
            InputStream is = this.getResources().openRawResource(R.raw.test1);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            int kk = 0;

            if (is != null) {
                try {
                    while ((data = bufferedReader.readLine()) != null) {
                        kk++;
                        String[] sArr = data.split(" ");
                        for (String sa : sArr) {
////                        System.out.println(sa);
//                        sBuffer.append(sa + "\n");
//                        System.out.println("from line :"+kk+" word : >>"+sa+"<<");
//                        for(int k = 0;k<sa.length();k++){
////                            System.out.println(">>"+sa.charAt(k)+"<<");
//                        }
                            v_trie.insert(sa);
                            System.out.println("from line :"+kk+" word : "+sa+" in trie: "+ v_trie.search(sa));

                        }
//                    sBuffer.append(data + "\n");
                    }
                    //textView.setText(sBuffer);
                    is.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            StringBuffer sBuffer_eng = new StringBuffer();
            InputStream is_eng = this.getResources().openRawResource(R.raw.enlish_trie_file);

            BufferedReader bufferedReader_eng = new BufferedReader(new InputStreamReader(is_eng));

            if (is_eng != null) {
                try {
                    while ((data = bufferedReader_eng.readLine()) != null) {
                        String[] sArr = data.split(" ");
                        for (String sa : sArr) {
////                        System.out.println(sa);
//                        sBuffer_eng.append(sa + "\n");
                            sa = sa.toLowerCase();
                            e_trie.insert(sa);

                        }
//                    sBuffer_eng.append(data + "\n");
//                    sBuffer_eng.append(data + "\n");
                    }
                    //textView.setText(sBuffer);
                    is_eng.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
////        System.out.println("sBuffer_eng   "+ sBuffer_eng);

            /////////////////////////////////// For Data COllection ////////////////
//        File folder = new File( Environment.getExternalStorageDirectory() + "/MyFileStorage" );

////        System.out.println(Environment.getExternalStorageDirectory() + "/MyFileStorage");
////        System.out.println(Environment.getExternalStorageDirectory() + filepath);
////        System.out.println(getExternalFilesDir(filepath));

            boolean success = true;
//        if (!folder.exists()) {
//            success = folder.mkdir();
//        }
////        System.out.println("successs   " + success);

//        if(success) {}


            // ---------------- New Approach ---------------
            init_tree = false;
        }


        //for user written words loading
        //for personalization
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
//            System.out.println(" ## ** -- Not Ready to write");
//            System.out.println(" isExternalStorageAvailable " + isExternalStorageAvailable() + "  isExternalStorageReadOnly  " + isExternalStorageReadOnly());
        } else {
//            System.out.println(" ## ** -- Ready to write");

            File root = Environment.getExternalStorageDirectory();
            myExternalFile = new File(root.getAbsolutePath() + "/EkusheKeyboard");

            if (myExternalFile.exists()) {
//                System.out.println(" <<<<<<<< --------- File exists!");
//                System.out.println("myExternalFile  "+ myExternalFile);
                File myExternalFile1 = new File(root.getAbsolutePath() + "/EkusheKeyboard/"+ filename );
                File myExternalFile2 = new File(root.getAbsolutePath() + "/EkusheKeyboard/"+ filename_eng );
                if(!myExternalFile1.exists()){
                    System.out.println(">>>>>>>>>>>>>>>no file >>>>>>>>>>>>>>>>>>>");
                    keyboardDataFile = new File(myExternalFile, filename);

                    try {
                        FileOutputStream f = new FileOutputStream(keyboardDataFile);
                        PrintWriter pw = new PrintWriter(f);
                        pw.println(" ");
                        pw.flush();
                        pw.close();
                        f.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.i("ranit", "******* File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(!myExternalFile2.exists()){
                    keyboardDataFileEng = new File(myExternalFile, filename_eng);

                    try {
                        FileOutputStream f_eng = new FileOutputStream(keyboardDataFileEng);
                        PrintWriter pw_bng = new PrintWriter(f_eng);
                        pw_bng.println(" ");
                        pw_bng.flush();
                        pw_bng.close();
                        f_eng.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.i("ranit", "******* File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    keyboardDataFile = new File(myExternalFile, filename);
                    FileInputStream fis = new FileInputStream(keyboardDataFile);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    HashMap<String, Integer>words_freq = new HashMap<>();
                    while((line=br.readLine())!=null) {
                        //here
                        String words[] = line.split(" ");
                        for(String e:words){
                            if(words_freq.get(e)==null){
                                words_freq.put(e,1);
                            }
                            else {
                                int cur_freq = words_freq.get(e);
                                words_freq.put(e, cur_freq + 1);
                            }
                            if(words_freq.get(e)>5 && v_trie.search(e) == false){
                                v_trie.insert(e);
                            }
                        }
                    }
                    fis.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.i("ranit", "******* File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    keyboardDataFileEng = new File(myExternalFile, filename_eng);
                    FileInputStream fis_eng = new FileInputStream(keyboardDataFileEng);
                    InputStreamReader isr_eng = new InputStreamReader(fis_eng);
                    BufferedReader br_eng = new BufferedReader(isr_eng);
                    String line_eng;
                    HashMap<String, Integer> words_freq_eng = new HashMap<>();
                    while ((line_eng = br_eng.readLine()) != null) {
                        //here
                        String words[] = line_eng.split(" ");
                        for (String e : words) {
                            if (words_freq_eng.get(e) == null) {
                                words_freq_eng.put(e, 1);
                            } else {
                                int cur_freq = words_freq_eng.get(e);
                                words_freq_eng.put(e, cur_freq + 1);
                            }
                            if (words_freq_eng.get(e) > 5 && e_trie.search(e) == false) {
                                e_trie.insert(e);
                            }
                        }
                    }
                    fis_eng.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.i("ranit", "******* File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                myExternalFile.mkdirs();
                keyboardDataFile = new File(myExternalFile, filename);
                keyboardDataFileEng = new File(myExternalFile,filename_eng);

                try {
                    FileOutputStream f = new FileOutputStream(keyboardDataFile);
                    PrintWriter pw = new PrintWriter(f);
                    pw.println(" ");
                    pw.flush();
                    pw.close();
                    f.close();
                    FileOutputStream f_eng = new FileOutputStream(keyboardDataFileEng);
                    PrintWriter pw_bng = new PrintWriter(f_eng);
                    pw_bng.println(" ");
                    pw_bng.flush();
                    pw_bng.close();
                    f_eng.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.i("ranit", "******* File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                System.out.println("Create FIle ends ------- write_dc_information ---- write_dc_information");
            }

        }


//        try {
//            String st_write = "Bangla Swype keyboard!\n";
//            FileOutputStream fOut = openFileOutput()
//            fOut.write(st_write.getBytes());
//            fOut.close();
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
        /////////////////////////////////// For Data COllection ////////////////

//        System.out.println("Search Result bangla " + v_trie.search("bangla"));
//        System.out.println("Search Result bangla " + v_trie.search("okotha"));
//        System.out.println("Search Result bangla " + v_trie.search("ongshi"));
//        System.out.println("Search Result bangla " + v_trie.search("ongs"));
//        System.out.println("Search Result bangla " + v_trie.search("okothon"));
//        System.out.println("Search Result bangla " + v_trie.search("ongshI"));
//        System.out.println("Search Result bangla " + v_trie.search("ongsI"));

////        System.out.println("Search Result algo " + e_trie.search("algo"));
////        System.out.println("Search Result alge " + e_trie.search("alge"));
////        System.out.println("Search Result also " + e_trie.search("also"));
////        System.out.println("Search Result to " + e_trie.search("to"));

        //Log.d("--------Trie------", "------Trie-------");
//        System.out.println("-------------------------------- Trie-----------------------------------------------------------" );

        /* Testing Trie importing from java netbeans */
        Log.d("onCreateInputView", "onCreateInputView");
        for(int i=0; i<30; i++) inarr[ i ] = 0;
        for(int i=0; i<100; i++) allSuggestedWOrds[i] = "";
        totalSuggestedWOrdsCount=0;
        sumOfinarr = 0;
        oncePrinted = false;

        SharedPreferences prefs = getSharedPreferences("test", MODE_PRIVATE);
        int flag = prefs.getInt("theme",0);

        if(flag != change){
            change = flag;
        }

        System.out.println("flag >>>>>>>> " + flag +" change>> "+change +">>> oncreate");

        try{
            if(flag == 0) {
                kv = (CustomKeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
            }
            else {
                kv = (CustomKeyboardView) getLayoutInflater().inflate(R.layout.keyboard_white, null);
            }
        }catch (Exception e){
            System.out.println(">>>>>>>exception>>> " + e);
        }

        kv.setPreviewEnabled(false);
        kv.setOnKeyboardActionListener(this);
        //mSymbolKeyboard = new
        kv.setKeyboard(mQwertyKeyboard);


        retrieveKeys();

        // Set the onTouchListener to be able to retrieve a MotionEvent
        kv.setOnTouchListener(new View.OnTouchListener() {
            @Override

            public boolean onTouch(View view, MotionEvent event) {

                //Log.d("onTouch", "onTouch");

                InputConnection ic = getCurrentInputConnection();
                for (Keyboard.Key key : keyList) {

                    if(printChar.length() > 50)
                    {
                        kv.ff = false;
                        return false;
                    }

                    Keyboard currentKeyboard = kv.getKeyboard();
                    if(currentKeyboard != mQwertyKeyboard && currentKeyboard != mQwertyEngKeyboard) break;

                    // If the coordinates from the Motion event are inside of the key
                    if (key.isInside((int) event.getX(), (int) event.getY())) {

                        // k is the key pressed
                        //Log.d("Debugging",
                        //        "Key pressed: X=" + k.x + " - Y=" + k.y);

                        int centreX, centreY;
                        centreX = (key.width/2) + key.x;
                        centreY = (key.height/2) + key.y;
//                        System.out.println(">>>>>>>>>>>>>>>> key.width >> "+key.width+">>> key.hight >" + key.height);
//                        System.out.println(">>>>>>>>>>>>>>>> key.x >> "+key.x+">>> key.y >" + key.y);

                        // These values are relative to the Keyboard View
                        //        Log.d("Debugging",
                        //        "Centre of the key pressed: X="+centreX+" - Y="+centreY);
                        //q in android

////                        System.out.println("event.getX() event.getY()  "+ event.getX()+" "+ event.getY() + " !!  key.x + key.y  " + key.x+ " "+ key.y + " " );

                        if(key.x == 0 && key.y == 0) {
                            //ic.commitText(String.valueOf((char) 113), 1);
                            keys[count++] = String.valueOf((char) 113);
                            printChar = printChar.concat( String.valueOf((char) 113) );
                            q++;
                            inarr[ 17 ]++;
                        }
                        else if(key.x == key.width && key.y == 0) {
                            //ic.commitText(String.valueOf((char) 119), 1);
                            keys[count++] = String.valueOf((char) 119);
                            printChar = printChar.concat( String.valueOf((char) 119) );
                            w++;
                            inarr[ 23 ]++;
                        }
                        else if(key.x == (key.width*2) && key.y == 0) {
                            //ic.commitText(String.valueOf((char) 101), 1);
                            keys[count++] = String.valueOf((char) 101);
                            printChar = printChar.concat( String.valueOf((char) 101) );
                            e++;
                            inarr[ 5 ]++;

                        }
                        else if(key.x == (key.width*3) && key.y == 0){
                            //ic.commitText(String.valueOf((char) 114), 1);
                            keys[count++] = String.valueOf((char) 114);
                            printChar = printChar.concat( String.valueOf((char) 114) );
                            r++;
                            inarr[ 18 ]++;
                        }
                        else if(key.x == (key.width*4) && key.y == 0){
                            //ic.commitText(String.valueOf((char) 116), 1);
                            keys[count++] = String.valueOf((char) 116);
                            printChar = printChar.concat( String.valueOf((char) 116) );
                            t++;
                            inarr[ 20 ]++;
                        }
                        else if(key.x == (key.width*5) && key.y == 0){
                            //ic.commitText(String.valueOf((char) 121), 1);
                            keys[count++] = String.valueOf((char) 121);
                            printChar = printChar.concat( String.valueOf((char) 121) );
                            y++;
                            inarr[ 25 ]++;
                        }
                        else if(key.x == (key.width*6) && key.y == 0){
                            //ic.commitText(String.valueOf((char) 117), 1);
                            keys[count++] = String.valueOf((char) 117);
                            printChar = printChar.concat( String.valueOf((char) 117) );
                            u++;
                            inarr[ 21 ]++;
                        }
                        else if(key.x == (key.width*7) && key.y == 0){
                            //ic.commitText(String.valueOf((char) 105), 1);
                            keys[count++] = String.valueOf((char) 105);
                            printChar = printChar.concat( String.valueOf((char) 105 ) );
                            i++;
                            inarr[ 9 ]++;
                        }
                        else if(key.x == (key.width*8) && key.y == 0){
                            //ic.commitText(String.valueOf((char) 111), 1);
                            keys[count++] = String.valueOf((char) 111);
                            printChar = printChar.concat( String.valueOf((char) 111) );
                            o++;
                            inarr[ 15 ]++;
                        }
                        else if(key.x == (key.width*9) && key.y == 0){
                            //ic.commitText(String.valueOf((char) 112), 1);
                            keys[count++] = String.valueOf((char) 112);
                            printChar = printChar.concat( String.valueOf((char) 112) );
                            p++;
                            inarr[ 16 ]++;
                        }
                        else if(key.x == key.width/2 && key.y == (key.height*1)) {
                            //ic.commitText(String.valueOf((char) 97), 1);
                            keys[count++] = String.valueOf((char) 97);
                            printChar = printChar.concat( String.valueOf((char) 97) );
                            a++;
                            inarr[ 1 ]++;
                        }
                        else if(key.x == (key.width/2)+key.width && key.y == (key.height*1)) {
                            //ic.commitText(String.valueOf((char) 115), 1);
                            keys[count++] = String.valueOf((char) 115);
                            printChar = printChar.concat( String.valueOf((char) 115) );
                            s++;
                            inarr[ 19 ]++;
                        }
                        else if(key.x == ((key.width/2)+key.width*2) && key.y == (key.height*1)) {
                            //ic.commitText(String.valueOf((char) 100), 1);
                            keys[count++] = String.valueOf((char) 100);
                            printChar = printChar.concat( String.valueOf((char) 100) );
                            d++;
                            inarr[ 4 ]++;
                        }
                        else if(key.x == ((key.width/2)+key.width*3) && key.y == (key.height*1)){
                            //ic.commitText(String.valueOf((char) 102), 1);
                            keys[count++] = String.valueOf((char) 102);
                            printChar = printChar.concat( String.valueOf((char) 102 ) );
                            f++;
                            inarr[ 6 ]++;
                        }
                        else if(key.x == ((key.width/2)+key.width*4) && key.y == (key.height*1)){
                            //ic.commitText(String.valueOf((char) 103), 1);
                            keys[count++] = String.valueOf((char) 103);
                            printChar = printChar.concat( String.valueOf((char) 103) );
                            g++;
                            inarr[ 7 ]++;
                        }
                        else if(key.x == ((key.width/2)+key.width*5) && key.y == (key.height*1)){
                            //ic.commitText(String.valueOf((char) 104), 1);
                            keys[count++] = String.valueOf((char) 104);
                            printChar = printChar.concat( String.valueOf((char) 104) );
                            h++;
                            inarr[ 8 ]++;
                        }
                        else if(key.x == ((key.width/2)+key.width*6) && key.y == (key.height*1)){
                            //ic.commitText(String.valueOf((char) 106), 1);
                            keys[count++] = String.valueOf((char) 106);
                            printChar = printChar.concat( String.valueOf((char) 106) );
                            j++;
                            inarr[ 10 ]++;
                        }
                        else if(key.x == ((key.width/2)+key.width*7) && key.y == (key.height*1)){
                            //ic.commitText(String.valueOf((char) 107), 1);
                            keys[count++] = String.valueOf((char) 107);
                            printChar = printChar.concat( String.valueOf((char) 107) );
                            k++;
                            inarr[ 11 ]++;
                        }
                        else if(key.x == ((key.width/2)+key.width*8) && key.y == (key.height*1)){
                            //ic.commitText(String.valueOf((char) 108), 1);
                            keys[count++] = String.valueOf((char) 108);
                            printChar = printChar.concat( String.valueOf((char) 108) );
                            l++;
                            inarr[ 12 ]++;
                        }
//                        else if(key.x == (key.width*9) && key.y == (key.height*1)){
//                            //ic.commitText(String.valueOf((char) 108), 1);
//                            keys[count++] = String.valueOf((char) 2509);
//                            printChar = printChar.concat( String.valueOf('`') );
//                            //l++;
//                            inarr[ 27 ]++;
//                        }
                        else if(key.x == (key.width/2)+key.width && key.y == (key.height*2)) {
                            //ic.commitText(String.valueOf((char) 122), 1);
                            keys[count++] = String.valueOf((char) 122);
                            printChar = printChar.concat( String.valueOf((char) 122) );
                            z++;
                            inarr[ 26 ]++;
                        }
                        else if(key.x == ((key.width/2)+key.width*2) && key.y == (key.height*2)) {
                            //ic.commitText(String.valueOf((char) 120), 1);
                            keys[count++] = String.valueOf((char) 120);
                            printChar = printChar.concat( String.valueOf((char) 120) );
                            x++;
                            inarr[ 24 ]++;
                        }
                        else if(key.x == ((key.width/2)+key.width*3) && key.y == (key.height*2)){
                            //ic.commitText(String.valueOf((char) 99), 1)
                            keys[count++] = String.valueOf((char) 99);
                            printChar = printChar.concat( String.valueOf((char) 99) );
                            c++;
                            inarr[ 3 ]++;
                        }
                        else if(key.x == ((key.width/2)+key.width*4) && key.y == (key.height*2)){
                            //ic.commitText(String.valueOf((char) 118), 1);
                            keys[count++] = String.valueOf((char) 118);
                            printChar = printChar.concat( String.valueOf((char) 118) );
                            v++;
                            inarr[ 22 ]++;
                        }
                        else if(key.x == ((key.width/2)+key.width*5) && key.y == (key.height*2)){
                            //ic.commitText(String.valueOf((char) 98), 1);
                            keys[count++] = String.valueOf((char) 98);
                            printChar = printChar.concat( String.valueOf((char) 98) );
                            b++;
                            inarr[ 2 ]++;
                        }
                        else if(key.x == ((key.width/2)+key.width*6) && key.y == (key.height*2)){
                            //ic.commitText(String.valueOf((char) 110), 1);
                            keys[count++] = String.valueOf((char) 110);
                            printChar = printChar.concat( String.valueOf((char) 110) );
                            n++;
                            inarr[ 14 ]++;

                        }
                        else if(key.x == ((key.width/2)+key.width*7) && key.y == (key.height*2)) {
                            //ic.commitText(String.valueOf((char) 109), 1);
                            keys[count++] = String.valueOf((char) 109);
                            printChar = printChar.concat( String.valueOf((char) 109) );
                            m++;
                            inarr[ 13 ]++;
                        }
                        else{
//                            System.out.println(">>>>>>>>>>on press code       >> "+ onpress_code);
                            if(onpress_code >=97 && onpress_code <= 122) {
                                keys[count++] = String.valueOf((char) onpress_code);
                                printChar = printChar.concat(String.valueOf((char) onpress_code));
                            }
                        }
                    }
                }

                // For each key in the key list
                // Return false to avoid consuming the touch event
//                System.out.println("printchar in on createInputview>>> "+printChar);

//                long eventDuration = event.getEventTime() - event.getDownTime();


                return false;
            }
        });
//        System.out.print("printchar in on createInputview>> "+printChar);
        kv.ff = true;

        System.out.println("<<                              onCreateinputview ends                                                >>");

        return kv;
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
// Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        this.attribute = attribute;
        mComposing.setLength(0);

        mCompletionOn = false;

        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
                mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
            case InputType.TYPE_TEXT_VARIATION_URI:
                mCurKeyboard = mQwertyEngKeyboard;
                break;

            case InputType.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
//                System.out.println("tEEEEEEEEEEEEEEEEEEEXXXXXXXXXXXXXXXXXT");
                if(lastKeyboard == null || lastKeyboard == mQwertyKeyboard)
                    mCurKeyboard = mQwertyKeyboard;
                else if(lastKeyboard == mQwertyEngKeyboard)
                    mCurKeyboard = mQwertyEngKeyboard;
                else if(lastKeyboard == mProvatKeyboard || lastKeyboard == mProvatKeyboardShift)
                    mCurKeyboard = mProvatKeyboard;

                // We now look for a few special variations of text that will
                // modify our behavior.
                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                    mCompletionOn = isFullscreenMode();
                }

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                if(lastKeyboard == null || lastKeyboard == mQwertyKeyboard)
                    mCurKeyboard = mQwertyKeyboard;
                else
                    mCurKeyboard = mQwertyEngKeyboard;
        }
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    // overlap solution
    @Override
    public void onComputeInsets(Insets outInsets) {
        super.onComputeInsets(outInsets);
        if (!isFullscreenMode()) {
            outInsets.contentTopInsets = outInsets.visibleTopInsets;
        }
    }

    //for provat
    void addToComposing(int a,int b){
        mComposing_bn_ver.append((char) a);
        mComposing_bn_ver.append((char) b);

        String k = mComposing_bn_ver.toString();

        PhoneticParserBTA avro_bta = PhoneticParserBTA.getInstance();
        avro_bta.setLoader(new PhoneticXmlLoaderBTA(context,false));
        String kk = avro_bta.parse(k);

        mComposing = new StringBuilder(kk);
        oncePrinted = true;
    }

//    public static void commitGifImage(Uri contentUri, String imageDescription) {
//        InputContentInfoCompat inputContentInfo = new InputContentInfoCompat(contentUri, new ClipDescription(imageDescription, new String[]{"image/png"}));
//        InputConnection inputConnection = getCurrentInputConnection();
//        EditorInfo editorInfo = getCurrentInputEditorInfo();
//        int flags = 0;
//        if (android.os.Build.VERSION.SDK_INT >= 25) {
//            flags |= InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
//        }
//        InputConnectionCompat.commitContent(
//                inputConnection, editorInfo, inputContentInfo, flags, opts);
//    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
//        Log.d("onKey", "onKey");
        System.out.println("onKey(int primaryCode, int[] keyCodes) starts   primaryCode" +primaryCode);

        cur_time_onkey = android.os.SystemClock.elapsedRealtime();

//        System.out.println("ON Key  -- cur_time_onkey   -- >  "+ cur_time_onkey);

        long duration;
        duration = cur_time_onkey - cur_time_onpress;

//        System.out.println("ON Key  -- cur_time_duration   -- >  "+ duration);

        if (isWordSeparator(primaryCode)) {
            System.out.println(">>>>>>>>>Asdasdas");
            // Handle separator
            if(primaryCode == 32 ) {
//                System.out.println("581    space   "+ primaryCode);
                ic.commitText( String.valueOf( (char) primaryCode ), 1 );

                Keyboard current = kv.getKeyboard();
                //for saving written words
                if(current == mQwertyKeyboard && mComposing.length()>1 && v_trie.search(mComposing.toString()) == false) {
                    try {
                        File root = Environment.getExternalStorageDirectory();
                        myExternalFile = new File(root.getAbsolutePath() + "/EkusheKeyboard");
                        keyboardDataFile = new File(myExternalFile, filename);
                        FileInputStream fis = new FileInputStream(keyboardDataFile);
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader br = new BufferedReader(isr);
                        String line;
                        StringBuilder sb = new StringBuilder("");
                        while((line=br.readLine())!=null) {
                            sb.append(line);
                        }
                        sb.append(" ");
                        sb.append(mComposing.toString());
                        fis.close();
                        System.out.println(">>>>>>>>>>>>>>"+sb.toString());
                        FileOutputStream f = new FileOutputStream(keyboardDataFile);
                        PrintWriter pw = new PrintWriter(f);
                        pw.write(sb.toString());
                        pw.flush();
                        pw.close();
                        f.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.i("ranit", "******* File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(current == mQwertyEngKeyboard && mComposing.length()>1 && e_trie.search(mComposing.toString()) == false) {
                    try {
                        File root = Environment.getExternalStorageDirectory();
                        myExternalFile = new File(root.getAbsolutePath() + "/EkusheKeyboard");
                        keyboardDataFileEng = new File(myExternalFile, filename_eng);
                        FileInputStream fis = new FileInputStream(keyboardDataFileEng);
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader br = new BufferedReader(isr);
                        String line;
                        StringBuilder sb = new StringBuilder("");
                        while((line=br.readLine())!=null) {
                            sb.append(line);
                        }
                        sb.append(" ");
                        sb.append(mComposing.toString());
                        fis.close();
                        System.out.println(">>>>>>>>>>>>>>"+sb.toString());
                        FileOutputStream f = new FileOutputStream(keyboardDataFileEng);
                        PrintWriter pw = new PrintWriter(f);
                        pw.write(sb.toString());
                        pw.flush();
                        pw.close();
                        f.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.i("ranit", "******* File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }



                // for shifting after '.'
                if(current == mQwertyEngKeyboard) {

                    try{
                        String beforeCursor = (String) ic.getTextBeforeCursor(2, 0);
//                    System.out.println(">>>>>>>>before Cursor >>>>" + beforeCursor + "<<<<");

                        if(beforeCursor.length() == 2) {
                            if (beforeCursor.charAt(0) == '.' && beforeCursor.charAt(1) == ' ') {
                                if (!caps) {
                                    handleShift();
                                    if (capsLocked)
                                        capsLocked = false;
                                }
                            }
                        }
                    }catch (Exception e){
                        System.out.println("Exception " + e);
                    }
                }

//                System.out.println(" mComposing_bn_ver  " + mComposing_bn_ver.toString() + "   " + mComposing_bn_ver.length() + "  "+mComposing.length());

                if( islastWordSwipe ) {

//                    System.out.println("call of for swipe next_words_show( lastWrittenWordWithSwipe )  " + lastWrittenWordWithSwipe);
                    next_words_show( lastWrittenWordWithSwipe );


                    islastWordSwipe = false;
                    isLastWordSwipdeAndAnSpaceTapped = true;
                }
                else {
                    isLastWordSwipdeAndAnSpaceTapped = false;
                    islastWordSwipe =false;
                    isNexWord = false;

                    if(mComposing_bn_ver.length() > 0) {
                        String bnVersion_st = mComposing_bn_ver.toString();
                        mComposing.setLength(0);
                        mComposing_bn_ver.setLength(0);
                        isLastWordSwipdeAndAnSpaceTapped = false;

//                        System.out.println(" next_words_show(bnVersion_st)  " + mComposing_bn_ver.toString() + "   bnVersion_st  " + bnVersion_st);


                        if(next_word_3gram == true){
                            String combined_2gram = last_word_3gram;
                            combined_2gram = combined_2gram + " " + bnVersion_st;
                            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + combined_2gram);
                            next_words_show(combined_2gram);

                            if(possibleNextWOrdsInBng.size() <= 0) {
                                next_words_show(bnVersion_st);
                            }
                        }
                        else
                            next_words_show(bnVersion_st);

                        sugg_flag = false;

                        last_word_3gram = bnVersion_st;
                        next_word_3gram = true;

//                        System.out.println(" 612 ---  possibleNextWOrdsInBng.size() " + possibleNextWOrdsInBng.size());

                        if(possibleNextWOrdsInBng.size() <= 0) {

//                            next_words_show("");

                            mCandidateView.setSuggestions(emojis, true, true);
//                            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>koi be");

                        }
                    }
                    else if(mComposing.length() >0){
                        String enVersion_st = mComposing.toString();
//                        System.out.println("mcomposing >>>>>>>"+mComposing.toString()+"<<<<<<<<<<<");

                        if(mComposing.toString().length() == 1 && mComposing.toString().charAt(0) == 'i'){
                            ic.deleteSurroundingText(2, 0);
                            char code1 = mComposing.toString().charAt(0);
                            code1 = Character.toUpperCase(code1);
//                            System.out.println("mcomposing >>>>>>>"+mComposing.toString() + "  "+code1+"<<<<<<<<<<<");
                            ic.commitText(String.valueOf(code1),1);
                            ic.commitText( String.valueOf( (char) primaryCode ), 1 );
                        }

                        mComposing.setLength(0);
                        mComposing_bn_ver.setLength(0);
                        isLastWordSwipdeAndAnSpaceTapped = false;

//                        System.out.println(" next_words_show(enVersion_st)  " + mComposing.toString() + "   enVersion_st  " + enVersion_st);

                        next_words_show_eng(enVersion_st);
                        sugg_flag = false;

//                        System.out.println(" 612 ---  possibleNextWOrdsInEng.size() " + possibleNextWOrdsInEng.size());

                        if(possibleNextWOrdsInEng.size() <= 0) {
                            sugg_flag = true;
                            mCandidateView.setSuggestions(emojis, true, true);
                            setCandidatesViewShown(true);

                        }
                    }
                    else {
//                        setCandidatesViewShown(false);
                        sugg_flag = true;
                        mCandidateView.setSuggestions(emojis, true, true);
                        setCandidatesViewShown(true);
                    }
                }
            }
            else {
                last_word_3gram = "";
                next_word_3gram = false;

                if (mComposing.length() > 0) {
                    commitTyped(getCurrentInputConnection());
                }
//                Keyboard current = kv.getKeyboard();
//                if(current == mQwertyEngKeyboard) {
//                    if (primaryCode == 46) {
//                        if (!caps) {
//                            handleShift();
//                            if (capsLocked)
//                                capsLocked = false;
//                        }
//                    }
//                }
                if(primaryCode == Keyboard.KEYCODE_DONE || primaryCode == 10) {
                    if(duration > 400){
                        final IBinder token = getWindow().getWindow().getAttributes().token;
                        mInputMethodManager.setInputMethod(token,"com.google.android.googlequicksearchbox/com.google.android.voicesearch.ime.VoiceInputMethodService");
                    }
                }
                if(duration > 400 && (primaryCode == 2551 || primaryCode == 46)){
                    ic.commitText(String.valueOf((char)44), 1);
                }
                else
                    sendKey(primaryCode);
//                setCandidatesViewShown(false);
            }

//            updateShiftKeyState(getCurrentInputEditorInfo()); //536
        }
        //for bangla provat
        else if(primaryCode>=2432 && primaryCode <= 2527){
            ic.commitText(String.valueOf((char) primaryCode),1 );

            mComposing_bn_ver.append((char) primaryCode);
            System.out.println(">>>>>>>>>>>>mcomposing_bn_ver for provat "+mComposing_bn_ver);

            String k = mComposing_bn_ver.toString();

            PhoneticParserBTA avro_bta = PhoneticParserBTA.getInstance();
            avro_bta.setLoader(new PhoneticXmlLoaderBTA(context,false));
            String kk = avro_bta.parse(k);
            System.out.println(">>>>>>>>>>>>mComposing for provat "+kk+"<<<<");

            mComposing = new StringBuilder(kk);

            oncePrinted = true;

        }
        //for emojis
        else if(primaryCode>120000){
            System.out.println(">>>>>>>>>>>>>>>>> emoji");
            String emoji = getEmojiByUnicode(primaryCode);

            if(primaryCode == 127463){
                ic.commitText(emoji,1);
//                ic.commitText("",1);
                emoji = getEmojiByUnicode(127465);
                ic.commitText(emoji,1);
            }else {
                ic.commitText(emoji, 1);
            }

//            // Send message here
//            Intent pictureMessageIntent = new Intent(Intent.ACTION_SEND);
//            pictureMessageIntent.setType("image/png");
//            Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/drawable/app_logo");
//            pictureMessageIntent.putExtra(Intent.EXTRA_STREAM, uri);
//            startActivity(pictureMessageIntent);

//            MyKeyboard.this.doCommitContent("A droid logo", MIME_TYPE_PNG, mPngFile);
        }
        //for popup in provat
        else if(primaryCode > 24530){
            if(primaryCode == 24701){
                ic.commitText(String.valueOf((char) 2470),1);
                ic.commitText(String.valueOf((char) 2494),1);

                addToComposing(2470,2494);
            }
            else if(primaryCode == 24702){
                ic.commitText(String.valueOf((char) 2470),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2470,2495);
            }
            else if(primaryCode == 24703){
                ic.commitText(String.valueOf((char) 2470),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2470,2497);
            }
            else if(primaryCode == 24704){
                ic.commitText(String.valueOf((char) 2470),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2470,2503);
            }

            //t
            else if(primaryCode == 24631){
                ic.commitText(String.valueOf((char) 2463),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2463,2494);
            }
            else if(primaryCode == 24632){
                ic.commitText(String.valueOf((char) 2463),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2463,2495);
            }
            else if(primaryCode == 24633){
                ic.commitText(String.valueOf((char) 2463),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2463,2497);
            }
            else if(primaryCode == 24634){
                ic.commitText(String.valueOf((char) 2463),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2463,2503);
            }

            //r
            else if(primaryCode == 24801){
                ic.commitText(String.valueOf((char) 2480),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2480,2494);
            }
            else if(primaryCode == 24802){
                ic.commitText(String.valueOf((char) 2480),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2480,2495);
            }
            else if(primaryCode == 24803){
                ic.commitText(String.valueOf((char) 2480),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2480,2497);
            }
            else if(primaryCode == 24804){
                ic.commitText(String.valueOf((char) 2480),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2480,2503);
            }
            else if(primaryCode == 24805){
                ic.commitText(String.valueOf((char) 2509),1);
                ic.commitText(String.valueOf((char) 2480),1);
                addToComposing(2509,2480);
            }

            //p
            else if(primaryCode == 24741){
                ic.commitText(String.valueOf((char) 2474),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2474,2494);
            }
            else if(primaryCode == 24742){
                ic.commitText(String.valueOf((char) 2474),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2474,2495);
            }
            else if(primaryCode == 24743){
                ic.commitText(String.valueOf((char) 2474),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2474,2497);
            }
            else if(primaryCode == 24744){
                ic.commitText(String.valueOf((char) 2474),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2474,2503);
            }

            //k
            else if(primaryCode == 24531){
                ic.commitText(String.valueOf((char) 2453),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2453,2494);
            }
            else if(primaryCode == 24532){
                ic.commitText(String.valueOf((char) 2453),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2453,2495);
            }
            else if(primaryCode == 24533){
                ic.commitText(String.valueOf((char) 2453),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2453,2497);
            }
            else if(primaryCode == 24534){
                ic.commitText(String.valueOf((char) 2453),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2453,2503);
            }
            else if(primaryCode == 24535){
                ic.commitText(String.valueOf((char) 2453),1);
                ic.commitText(String.valueOf((char) 2509),1);
                ic.commitText(String.valueOf((char) 2487),1);

                mComposing_bn_ver.append((char) 2453);
                mComposing_bn_ver.append((char) 2509);
                mComposing_bn_ver.append((char) 2487);

                String k = mComposing_bn_ver.toString();

                PhoneticParserBTA avro_bta = PhoneticParserBTA.getInstance();
                avro_bta.setLoader(new PhoneticXmlLoaderBTA(context,false));
                String kk = avro_bta.parse(k);

                mComposing = new StringBuilder(kk);
                oncePrinted = true;
            }

            //l
            else if(primaryCode == 24821){
                ic.commitText(String.valueOf((char) 2482),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2482,2494);
            }
            else if(primaryCode == 24822){
                ic.commitText(String.valueOf((char) 2482),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2482,2495);
            }
            else if(primaryCode == 24823){
                ic.commitText(String.valueOf((char) 2482),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2482,2497);
            }
            else if(primaryCode == 24824){
                ic.commitText(String.valueOf((char) 2482),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2482,2503);
            }



            //স
            else if(primaryCode == 24881){
                ic.commitText(String.valueOf((char) 2488),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2488,2494);
            }
            else if(primaryCode == 24882){
                ic.commitText(String.valueOf((char) 2488),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2488,2495);
            }
            else if(primaryCode == 24883){
                ic.commitText(String.valueOf((char) 2488),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2488,2497);
            }
            else if(primaryCode == 24884){
                ic.commitText(String.valueOf((char) 2488),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2488,2503);
            }
            else if(primaryCode == 24885){
                ic.commitText(String.valueOf((char) 2488),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2488,2496);
            }

            //ড
            else if(primaryCode == 24651){
                ic.commitText(String.valueOf((char) 2465),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2465,2494);
            }
            else if(primaryCode == 24652){
                ic.commitText(String.valueOf((char) 2465),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2465,2495);
            }
            else if(primaryCode == 24653){
                ic.commitText(String.valueOf((char) 2465),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2465,2497);
            }
            else if(primaryCode == 24654){
                ic.commitText(String.valueOf((char) 2465),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2465,2503);
            }
            else if(primaryCode == 24655){
                ic.commitText(String.valueOf((char) 2465),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2465,2496);
            }

            //ত
            else if(primaryCode == 24681){
                ic.commitText(String.valueOf((char) 2468),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2468,2494);
            }
            else if(primaryCode == 24682){
                ic.commitText(String.valueOf((char) 2468),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2468,2495);
            }
            else if(primaryCode == 24683){
                ic.commitText(String.valueOf((char) 2468),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2468,2497);
            }
            else if(primaryCode == 24684){
                ic.commitText(String.valueOf((char) 2468),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2468,2503);
            }
            else if(primaryCode == 24685){
                ic.commitText(String.valueOf((char) 2468),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2468,2496);
            }



            //গ
            else if(primaryCode == 24551){
                ic.commitText(String.valueOf((char) 2455),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2455,2494);
            }
            else if(primaryCode == 24552){
                ic.commitText(String.valueOf((char) 2455),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2455,2495);
            }
            else if(primaryCode == 24553){
                ic.commitText(String.valueOf((char) 2455),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2455,2497);
            }
            else if(primaryCode == 24554){
                ic.commitText(String.valueOf((char) 2455),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2455,2496);
            }


            //হ
            else if(primaryCode == 24891){
                ic.commitText(String.valueOf((char) 2489),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2489,2494);
            }
            else if(primaryCode == 24892){
                ic.commitText(String.valueOf((char) 2489),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2489,2495);
            }
            else if(primaryCode == 24893){
                ic.commitText(String.valueOf((char) 2489),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2489,2497);
            }
            else if(primaryCode == 24894){
                ic.commitText(String.valueOf((char) 2489),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2489,2503);
            }
            else if(primaryCode == 24895){
                ic.commitText(String.valueOf((char) 2489),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2489,2496);
            }
            else if(primaryCode == 24896){
                ic.commitText(String.valueOf((char) 2489),1);
                ic.commitText(String.valueOf((char) 2509),1);
                ic.commitText(String.valueOf((char) 2478),1);

                mComposing_bn_ver.append((char) 2489);
                mComposing_bn_ver.append((char) 2509);
                mComposing_bn_ver.append((char) 2478);

                String k = mComposing_bn_ver.toString();

                PhoneticParserBTA avro_bta = PhoneticParserBTA.getInstance();
                avro_bta.setLoader(new PhoneticXmlLoaderBTA(context,false));
                String kk = avro_bta.parse(k);

                mComposing = new StringBuilder(kk);
                oncePrinted = true;
            }



            //জ
            else if(primaryCode == 24601){
                ic.commitText(String.valueOf((char) 2460),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2460,2494);
            }
            else if(primaryCode == 24602){
                ic.commitText(String.valueOf((char) 2460),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2460,2495);
            }
            else if(primaryCode == 24603){
                ic.commitText(String.valueOf((char) 2460),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2460,2497);
            }
            else if(primaryCode == 24604){
                ic.commitText(String.valueOf((char) 2460),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2460,2503);
            }
            else if(primaryCode == 24605){
                ic.commitText(String.valueOf((char) 2460),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2460,2496);
            }

            //য়
            else if(primaryCode == 25271){
                ic.commitText(String.valueOf((char) 2527),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2427,2494);
            }
            else if(primaryCode == 25272){
                ic.commitText(String.valueOf((char) 2527),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2427,2495);
            }
            else if(primaryCode == 25273){
                ic.commitText(String.valueOf((char) 2527),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2427,2497);
            }
            else if(primaryCode == 25274){
                ic.commitText(String.valueOf((char) 2527),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2427,2503);
            }
            else if(primaryCode == 25275){
                ic.commitText(String.valueOf((char) 2527),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2427,2496);
            }


            //শ
            else if(primaryCode == 24861){
                ic.commitText(String.valueOf((char) 2486),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2486,2494);
            }
            else if(primaryCode == 24862){
                ic.commitText(String.valueOf((char) 2486),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2486,2495);
            }
            else if(primaryCode == 24863){
                ic.commitText(String.valueOf((char) 2486),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2486,2497);
            }
            else if(primaryCode == 24864){
                ic.commitText(String.valueOf((char) 2486),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2486,2503);
            }
            else if(primaryCode == 24865){
                ic.commitText(String.valueOf((char) 2486),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2486,2496);
            }

            //চ
            else if(primaryCode == 24581){
                ic.commitText(String.valueOf((char) 2458),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2458,2494);
            }
            else if(primaryCode == 24582){
                ic.commitText(String.valueOf((char) 2458),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2458,2495);
            }
            else if(primaryCode == 24583){
                ic.commitText(String.valueOf((char) 2458),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2458,2497);
            }
            else if(primaryCode == 24584){
                ic.commitText(String.valueOf((char) 2458),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2458,2503);
            }
            else if(primaryCode == 24585){
                ic.commitText(String.valueOf((char) 2458),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2458,2496);
            }


            //ব
            else if(primaryCode == 24761){
                ic.commitText(String.valueOf((char) 2476),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2476,2494);
            }
            else if(primaryCode == 24762){
                ic.commitText(String.valueOf((char) 2476),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2476,2495);
            }
            else if(primaryCode == 24763){
                ic.commitText(String.valueOf((char) 2476),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2476,2497);
            }
            else if(primaryCode == 24764){
                ic.commitText(String.valueOf((char) 2476),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2476,2503);
            }
            else if(primaryCode == 24765){
                ic.commitText(String.valueOf((char) 2476),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2476,2496);
            }

            //ন
            else if(primaryCode == 24721){
                ic.commitText(String.valueOf((char) 2472),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2472,2494);
            }
            else if(primaryCode == 24722){
                ic.commitText(String.valueOf((char) 2472),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2472,2495);
            }
            else if(primaryCode == 24723){
                ic.commitText(String.valueOf((char) 2472),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2472,2497);
            }
            else if(primaryCode == 24724){
                ic.commitText(String.valueOf((char) 2472),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2472,2503);
            }
            else if(primaryCode == 24725){
                ic.commitText(String.valueOf((char) 2472),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2472,2496);
            }

            //ম
            else if(primaryCode == 24781){
                ic.commitText(String.valueOf((char) 2478),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2478,2494);
            }
            else if(primaryCode == 24782){
                ic.commitText(String.valueOf((char) 2478),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2478,2495);
            }
            else if(primaryCode == 24783){
                ic.commitText(String.valueOf((char) 2478),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2478,2497);
            }
            else if(primaryCode == 24784){
                ic.commitText(String.valueOf((char) 2478),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2478,2503);
            }
            else if(primaryCode == 24785){
                ic.commitText(String.valueOf((char) 2478),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2478,2496);
            }


            //provat shift




            //ফ
            else if(primaryCode == 24751){
                ic.commitText(String.valueOf((char) 2475),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2475,2494);
            }
            else if(primaryCode == 24752){
                ic.commitText(String.valueOf((char) 2475),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2475,2495);
            }
            else if(primaryCode == 24753){
                ic.commitText(String.valueOf((char) 2475),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2475,2497);
            }
            else if(primaryCode == 24754){
                ic.commitText(String.valueOf((char) 2475),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2475,2503);
            }
            else if(primaryCode == 24755){
                ic.commitText(String.valueOf((char) 2475),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2475,2496);
            }
            else if(primaryCode == 24756){
                ic.commitText(String.valueOf((char) 2475),1);
                ic.commitText(String.valueOf((char) 2498),1);
                addToComposing(2475,2498);
            }


            //খ
            else if(primaryCode == 24541){
                ic.commitText(String.valueOf((char) 2454),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2454,2494);
            }
            else if(primaryCode == 24542){
                ic.commitText(String.valueOf((char) 2454),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2454,2495);
            }
            else if(primaryCode == 24543){
                ic.commitText(String.valueOf((char) 2454),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2454,2497);
            }
            else if(primaryCode == 24544){
                ic.commitText(String.valueOf((char) 2454),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2454,2503);
            }
            else if(primaryCode == 24545){
                ic.commitText(String.valueOf((char) 2454),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2454,2496);
            }

            //য
            else if(primaryCode == 24791){
                ic.commitText(String.valueOf((char) 2479),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2479,2494);
            }
            else if(primaryCode == 24792){
                ic.commitText(String.valueOf((char) 2479),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2479,2495);
            }
            else if(primaryCode == 24793){
                ic.commitText(String.valueOf((char) 2479),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2479,2497);
            }
            else if(primaryCode == 24794){
                ic.commitText(String.valueOf((char) 2479),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2479,2503);
            }
            else if(primaryCode == 24795){
                ic.commitText(String.valueOf((char) 2479),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2479,2496);
            }
            else if(primaryCode == 24796){
                ic.commitText(String.valueOf((char) 2509),1);
                ic.commitText(String.valueOf((char) 2479),1);
                addToComposing(2509,2479);
            }

            //ছ
            else if(primaryCode == 24591){
                ic.commitText(String.valueOf((char) 2459),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2459,2494);
            }
            else if(primaryCode == 24592){
                ic.commitText(String.valueOf((char) 2459),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2459,2495);
            }
            else if(primaryCode == 24593){
                ic.commitText(String.valueOf((char) 2459),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2459,2497);
            }
            else if(primaryCode == 24594){
                ic.commitText(String.valueOf((char) 2459),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2459,2503);
            }
            else if(primaryCode == 24595){
                ic.commitText(String.valueOf((char) 2459),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2459,2496);
            }


            //ভ
            else if(primaryCode == 24771){
                ic.commitText(String.valueOf((char) 2477),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2477,2494);
            }
            else if(primaryCode == 24772){
                ic.commitText(String.valueOf((char) 2477),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2477,2495);
            }
            else if(primaryCode == 24773){
                ic.commitText(String.valueOf((char) 2477),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2477,2497);
            }
            else if(primaryCode == 24774){
                ic.commitText(String.valueOf((char) 2477),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2477,2503);
            }
            else if(primaryCode == 24775){
                ic.commitText(String.valueOf((char) 2477),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2477,2496);
            }
            else if(primaryCode == 24776){
                ic.commitText(String.valueOf((char) 2477),1);
                ic.commitText(String.valueOf((char) 2498),1);
                addToComposing(2477,2498);
            }


            //ণ
            else if(primaryCode == 24671){
                ic.commitText(String.valueOf((char) 2467),1);
                ic.commitText(String.valueOf((char) 2494),1);
                addToComposing(2467,2494);
            }
            else if(primaryCode == 24672){
                ic.commitText(String.valueOf((char) 2467),1);
                ic.commitText(String.valueOf((char) 2495),1);
                addToComposing(2467,2495);
            }
            else if(primaryCode == 24673){
                ic.commitText(String.valueOf((char) 2467),1);
                ic.commitText(String.valueOf((char) 2497),1);
                addToComposing(2467,2497);
            }
            else if(primaryCode == 24674){
                ic.commitText(String.valueOf((char) 2467),1);
                ic.commitText(String.valueOf((char) 2503),1);
                addToComposing(2467,2503);
            }
            else if(primaryCode == 24675){
                ic.commitText(String.valueOf((char) 2467),1);
                ic.commitText(String.valueOf((char) 2496),1);
                addToComposing(2467,2496);
            }
            else if(primaryCode == 24676){
                ic.commitText(String.valueOf((char) 2467),1);
                ic.commitText(String.valueOf((char) 2498),1);
                addToComposing(2467,2498);
            }
        }
        else if(primaryCode == -500){
            Keyboard current = kv.getKeyboard();
            if(current == mQwertyKeyboard || current == mQwertyEngKeyboard)
                lastKeyboard = (CustomKeyboard) current;
            setLatinKeyboard(mEmojiKeyboard);

            setCandidatesViewShown(false);

            isLastWordSwipdeAndAnSpaceTapped = false;
            islastWordSwipe =false;
            isNexWord = false;
        }
        else if(primaryCode == -501){
            setLatinKeyboard(mEmoji2Keyboard);

            setCandidatesViewShown(false);

            isLastWordSwipdeAndAnSpaceTapped = false;
            islastWordSwipe =false;
            isNexWord = false;
        }
        else if(primaryCode == -502){
            setLatinKeyboard(mEmoji3Keyboard);

            setCandidatesViewShown(false);

            isLastWordSwipdeAndAnSpaceTapped = false;
            islastWordSwipe =false;
            isNexWord = false;
        }
        else if(primaryCode == -503){
            setLatinKeyboard(mEmoji4Keyboard);

            setCandidatesViewShown(false);

            isLastWordSwipdeAndAnSpaceTapped = false;
            islastWordSwipe =false;
            isNexWord = false;
        }
        else if(primaryCode == -504){
            setLatinKeyboard(mEmoji5Keyboard);

            setCandidatesViewShown(false);

            isLastWordSwipdeAndAnSpaceTapped = false;
            islastWordSwipe =false;
            isNexWord = false;
        }
//        primaryCode == Keyboard.KEYCODE_DELETE
        else if(primaryCode == -6 ) {

//            System.out.println(" ----------  638 -------  isNexWord    "  +  isNexWord + " islastWordSwipe  " + islastWordSwipe );
//            if(lastWrittenWordWithSwipe != null )System.out.println("   lastWrittenWordWithSwipe  " + lastWrittenWordWithSwipe + " "+ lastWrittenWordWithSwipe.length());

//            if( isSuggestionTapped) {
//
//                if( WordShownAfterTappingSuggestion != null) {
//                    int cur_len_LastSelectedSuggestedWord = WordShownAfterTappingSuggestion.length() + 1;
//
//
////                    System.out.println("647  nextWordShownAfterTapping    " + WordShownAfterTappingSuggestion.length() + "  nextWordShownAfterTapping   " + WordShownAfterTappingSuggestion);
//
//                    for (int i = 0; i < cur_len_LastSelectedSuggestedWord; i++) {
//                        ic.deleteSurroundingText(1, 0);
//                    }
//
//                }
////                else System.out.println( "654  nextWordShownAfterTapping  is NULL " + WordShownAfterTappingSuggestion);
//
//                islastWordSwipe = false;
//                isLastWordSwipdeAndAnSpaceTapped = false;
//                isNexWord = false;
//
//            }
            last_word_3gram = "";
            next_word_3gram = false;

            Keyboard current = kv.getKeyboard();
            if( islastWordSwipe ) {

                if(lastWrittenWordWithSwipe !=null ) {
                    int cur_len_lastSwipedWord = lastWrittenWordWithSwipe.length();

//                    System.out.println(" Last word was a swipe word now delete the whole word!    " + cur_len_lastSwipedWord);

                    for (int i = 0; i < cur_len_lastSwipedWord; i++) {
                        ic.deleteSurroundingText(1, 0);
                    }
                }
                islastWordSwipe = false;
                isLastWordSwipdeAndAnSpaceTapped = false;
                isNexWord =false;
            }
            //for suggestion after backspacing some letters eng
            else if(mComposing.length() == 0 && current == mQwertyEngKeyboard){
                try{
                    String beforeCursor = (String) ic.getTextBeforeCursor(1, 0);
                    int value = (int) beforeCursor.charAt(0);
                    if(value >= 50000)
                        ic.deleteSurroundingText(1, 0);
                }catch (Exception e){
                    System.out.println(">>> Exception "+e);
                }
                ic.deleteSurroundingText(1,0);
                try{
                    String last = (String) ic.getTextBeforeCursor(1,0);
                    int count = 1;
                    if(last.charAt(0) >= 'a' && last.charAt(0) <= 'z') {
                        while (true) {
                            String lastt = (String) ic.getTextBeforeCursor(count, 0);
                            String check_start = (String) ic.getTextBeforeCursor(count-1,0);
                            System.out.println(">>>>>>>>>>>>>>> text before >>>"+ lastt + "<<<<<");
                            if (lastt.charAt(0) == ' ' || (lastt.equals(check_start)) || !((lastt.charAt(0) >= 'a' && lastt.charAt(0) <= 'z') || (lastt.charAt(0) >= 'A' && lastt.charAt(0) <= 'Z'))) {
                                count--;
                                break;
                            }
                            count++;
                        }

                        // in case of any uppercase in the mcomposing
                        String k = (String) ic.getTextBeforeCursor(count,0);
                        String kk = "";
                        for(int i=0;i<k.length();i++){
                            kk = kk + Character.toLowerCase(k.charAt(i));
                        }

                        mComposing = new StringBuilder(kk);
                    }
                }catch (Exception e){
                    System.out.println("ERRor " + e);
                }
                oncePrinted = true;
            }
            //for suggestion after backspacing some letters bang 982 - 9e6 2434-2534
            else if(mComposing.length() == 0 && (current == mQwertyKeyboard || current == mProvatKeyboardShift || current == mProvatKeyboard)){
                try{
                    String beforeCursor = (String) ic.getTextBeforeCursor(1, 0);
                    int value = (int) beforeCursor.charAt(0);
                    if(value >= 50000)
                        ic.deleteSurroundingText(1, 0);
                }catch (Exception e){
                    System.out.println(">>> Exception "+e);
                }
                ic.deleteSurroundingText(1,0);
                try{
                    String last = (String) ic.getTextBeforeCursor(1,0);
                    int count = 1;
                    System.out.println(">>>>>bngbng " + (int)last.charAt(0));
                    if(last.charAt(0) >= 2434 && last.charAt(0) <= 2534) {
                        while (true) {
                            String lastt = (String) ic.getTextBeforeCursor(count, 0);
                            String check_start = (String) ic.getTextBeforeCursor(count-1,0);
                            System.out.println(">>>>>>>>>>>>>>> text before >>>"+ lastt + "<<<<<");
                            if (lastt.charAt(0) == ' ' || (lastt.equals(check_start)) || !(lastt.charAt(0) >= 2434 && lastt.charAt(0) <= 2534)) {
                                count--;
                                break;
                            }
                            count++;
                        }

                        // in case of any uppercase in the mcomposing
                        String k = (String) ic.getTextBeforeCursor(count,0);

                        PhoneticParserBTA avro_bta = PhoneticParserBTA.getInstance();
                        avro_bta.setLoader(new PhoneticXmlLoaderBTA(context,false));
                        String kk = avro_bta.parse(k);
                        System.out.println(">>>>>>>>>>>>"+kk+"<<<<");

                        mComposing = new StringBuilder(kk);
                        mComposing_bn_ver = new StringBuilder(k);
                    }
                }catch (Exception e){
                    System.out.println("ERRor " + e);
                }
                oncePrinted = true;

            }
            else {


                isLastWordSwipdeAndAnSpaceTapped = false;
                islastWordSwipe =false;
                isNexWord = false;


                if(current == mQwertyKeyboard || current == mProvatKeyboardShift || current == mProvatKeyboard) {

                    try{
                        String beforeCursor = (String) ic.getTextBeforeCursor(1, 0);
                        int value = (int) beforeCursor.charAt(0);
                        if(value >= 50000)
                            ic.deleteSurroundingText(1, 0);
                    }catch (Exception e){
                        System.out.println(">>> Exception "+e);
                    }

                    ic.deleteSurroundingText(1, 0);


                    if (mComposing_bn_ver.length() > 0) {
                        mComposing_bn_ver.deleteCharAt(mComposing_bn_ver.length() - 1);
                    }

                    if (mComposing.length() > 0) {

                        if(mComposing.length()>1){
                            String k = "";
                            k = k + mComposing.charAt(mComposing.length()-2);
                            k = k + mComposing.charAt(mComposing.length()-1);
                            if(k.equals("dh") || k.equals("bh") || k.equals("ch") || k.equals("gh") || k.equals("jh") || k.equals("kh") || k.equals("sh") || k.equals("ng") || k.equals("th") || k.equals("Th") || k.equals("ph") || k.equals("rr") || k.equals("Sh") || k.equals("TH") || k.equals("OU") || k.equals("OI")){
                                mComposing.deleteCharAt(mComposing.length()-1);
                            }
                        }

                        mComposing.deleteCharAt(mComposing.length() - 1);
                        if (mComposing_bn_ver.length() == 0) {
                            mComposing.setLength(0);
                        }
                        // Better solution is to keep an list of correspondent deletechar of mComposing comparing to ic input list will add 2 when adding h
                        handleLetDelofSugPref();
//                if(primaryKeyofOnPress != primaryCode ) laterCharofAKey = true;
//                else {
//
//                }
                    } else {
                        setCandidatesViewShown(false);
//                        mCandidateView.setSuggestions(emojis, true, true);
//                        setCandidatesViewShown(true);
                    }
                    System.out.println(">>>>>>>>>>>> mcomposing_bn_ver >>"+ mComposing_bn_ver.toString() + ">>>>>>>>>>>mcomposing >>"+mComposing.toString()+">>");

                }
                else{
                    try{
                        String beforeCursor = (String) ic.getTextBeforeCursor(1, 0);
                        int value = (int) beforeCursor.charAt(0);
                        if(value >= 50000)
                            ic.deleteSurroundingText(1, 0);
                    }catch (Exception e){
                        System.out.println(">>> Exception "+e);
                    }

                    ic.deleteSurroundingText(1, 0);
                    if(mComposing.length() > 0)
                        mComposing.deleteCharAt(mComposing.length() - 1);
                }
//                System.out.println("   Keyboard.KEYCODE_DELETE   ~~~~~~~~~ on key mComposing   " + mComposing);
                oncePrinted = true;


            }


            // after deleting
            if(current == mQwertyEngKeyboard) {

                try{
                    String beforeCursor = (String) ic.getTextBeforeCursor(2, 0);
//                System.out.println(">>>>>>>>before Cursor >>>>" + beforeCursor + "<<<<");

                    if (beforeCursor.length() == 0) {
                        if (!caps) {
                            handleShift();
                            if (capsLocked)
                                capsLocked = false;
                        }
                    } else if(beforeCursor.length() > 0) {
                        if (beforeCursor.charAt(0) == '.' && beforeCursor.charAt(1) == ' ') {
                            if (!caps) {
                                handleShift();
                                if (capsLocked)
                                    capsLocked = false;
                            }
                        } else {
                            if (caps) {
                                handleShift();
                                if (capsLocked)
                                    capsLocked = false;
                            }
                        }
                    }
                }catch(Exception e){
                    System.out.println("Exception " + e);
                }
            }

//            getCurrentInputConnection().deleteSurroundingText(1, 0);
        }
        else if(primaryCode == Keyboard.KEYCODE_SHIFT  ) {
//            caps = !caps;
//            mQwertyKeyboard.setShifted(caps);
//            kv.invalidateAllKeys();
            handleShift();
            if(capsLocked)
                capsLocked = false;
            isLastWordSwipdeAndAnSpaceTapped = false;
            islastWordSwipe =false;
            isNexWord = false;
        }
        else if( primaryCode == -400 ) {
//            caps = !caps;
//            mQwertyKeyboard.setShifted(caps);
//            kv.invalidateAllKeys();
            capsLocked= true;
        }
        else if( primaryCode == -5 ) {
//            caps = !caps;
//            mQwertyKeyboard.setShifted(caps);
//            kv.invalidateAllKeys();
            capsLocked= true;
        }
//        else if (primaryCode == kv.KEYCODE_LANGUAGE_SWITCH) {
//            handleLanguageSwitch();
//            return;
//        }
//        else if (primaryCode == LatinKeyboardView.KEYCODE_OPTIONS) {
//            // Show a menu or somethin'
//        }
        else if (primaryCode == codeForEngQwertyKeyboard && kv != null) {
            // Show next languageKeyboard
//            System.out.println("primaryCode == codeForEngQwertyKeyboard  && kv != null");

            Keyboard current = kv.getKeyboard();
            if(duration >= 400){
                if(current == mProvatKeyboard || current == mProvatKeyboardShift){
                    if(kv.isShifted()) {
                        kv.setShifted(false);
                        caps = !caps;
                    }
                    mComposing.setLength(0);
                    mComposing_bn_ver.setLength(0);

                    setLatinKeyboard(mQwertyEngKeyboard);
                    lastKeyboard = mQwertyEngKeyboard;
                }else {
                    if (kv.isShifted()) {
                        kv.setShifted(false);
                        caps = !caps;
                    }
                    mComposing.setLength(0);
                    mComposing_bn_ver.setLength(0);

                    setLatinKeyboard(mProvatKeyboard);
                    lastKeyboard = mProvatKeyboard;
                }
            }
            else if( current == mQwertyKeyboard ) {
                if(kv.isShifted()) {
                    kv.setShifted(false);
                    caps = !caps;
                }
                mComposing.setLength(0);
                mComposing_bn_ver.setLength(0);

                setLatinKeyboard(mQwertyEngKeyboard);

                sugg_flag = true;
                mCandidateView.setSuggestions(emojis, true, true);
                setCandidatesViewShown(true);

                /*handleShift();
                if(capsLocked)
                    capsLocked = false;*/

                lastKeyboard = mQwertyEngKeyboard;


                try{
                    // for shifting in empty or after fullstop
                    String beforeCursor = (String) ic.getTextBeforeCursor(2, 0);
                    System.out.println(">>>>>>>>before Cursor >>>>" + beforeCursor + "<<<<");


                    if (beforeCursor.length() == 0) {
                        if (!caps) {
                            handleShift();
                            if (capsLocked)
                                capsLocked = false;
                        }
                    } else if(beforeCursor.length() > 0) {
                        if (beforeCursor.charAt(0) == '.' && beforeCursor.charAt(1) == ' ') {
                            if (!caps) {
                                handleShift();
                                if (capsLocked)
                                    capsLocked = false;
                            }
                        } else {
                            if (caps) {
                                handleShift();
                                if (capsLocked)
                                    capsLocked = false;
                            }
                        }
                    }
                }catch (Exception e){
                    System.out.println("Exception " + e);
                }



//                mSymbolsKeyboard.setShifted(false);
//                setCandidatesViewShown(false);
            }
            else if( current == mQwertyEngKeyboard ) {
                if(kv.isShifted()) {
                    kv.setShifted(false);
                    caps = !caps;
                }

                mComposing.setLength(0);
                mComposing_bn_ver.setLength(0);

                setLatinKeyboard(mQwertyKeyboard);
                lastKeyboard = mQwertyKeyboard;
//                mSymbolsKeyboard.setShifted(false);
//                setCandidatesViewShown(false);
            }
            else if(current == mProvatKeyboard || current == mProvatKeyboardShift){
                if(kv.isShifted()) {
                    kv.setShifted(false);
                    caps = !caps;
                }

                mComposing.setLength(0);
                mComposing_bn_ver.setLength(0);

                setLatinKeyboard(mQwertyKeyboard);
                lastKeyboard = mQwertyKeyboard;
            }
            else if(current == mEmojiKeyboard || current ==mEmoji2Keyboard || current == mEmoji3Keyboard || current == mEmoji4Keyboard || current == mEmoji5Keyboard){
                if(lastKeyboard == null)
                    setLatinKeyboard(mSymbolsBngKeyboard);
                else
                    setLatinKeyboard(lastKeyboard);
            }

            isLastWordSwipdeAndAnSpaceTapped = false;
            islastWordSwipe =false;
            isNexWord = false;
        }
        else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE && kv != null) {
//            System.out.println("primaryCode == Keyboard.KEYCODE_MODE_CHANGE  && mInputView != null");


            Keyboard current = kv.getKeyboard();
//            if(duration > 400){
//                final IBinder token = getWindow().getWindow().getAttributes().token;
//                mInputMethodManager.setInputMethod(token,"com.google.android.googlequicksearchbox/com.google.android.voicesearch.ime.VoiceInputMethodService");
//            }
//            else
            if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard || current == mSymbolsBngKeyboard) {

//                System.out.println("current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard");
                if(lastKeyboard == null || lastKeyboard == mQwertyKeyboard) {
                    setLatinKeyboard(mQwertyKeyboard);
                }
                else if(lastKeyboard == mProvatKeyboard || lastKeyboard == mProvatKeyboardShift){
                    setLatinKeyboard(mProvatKeyboard);
                }
                else {
                    setLatinKeyboard(mQwertyEngKeyboard);
                }
                mCandidateView.setSuggestions(emojis, true, true);
                setCandidatesViewShown(true);
            } else {

//                System.out.println("setLatinKeyboard(mSymbolsKeyboard)  + mSymbolsKeyboard.setShifted(false);");
                if(current == mQwertyKeyboard) {
                    setLatinKeyboard(mSymbolsBngKeyboard);
                    mSymbolsBngKeyboard.setShifted(false);
                }
                else{

                    setLatinKeyboard(mSymbolsKeyboard);
                    mSymbolsKeyboard.setShifted(false);
                }
                mCandidateView.setSuggestions(emojis, true, true);
                setCandidatesViewShown(true);
//                setCandidatesViewShown(false);
//                mComposing.setLength(0);
            }

            isLastWordSwipdeAndAnSpaceTapped = false;
            islastWordSwipe =false;
            isNexWord = false;
            mComposing_bn_ver.setLength(0);
            mComposing.setLength(0);

        }
        else {
//            System.out.println("handleCharacter(primaryCode, keyCodes);");
//            handleCharacter(primaryCode, keyCodes);
            char code = (char)primaryCode;
            Log.d("print onkey", (String.valueOf(code)));
            if(printChar.length()==0){
                if(code >=97 && code <= 122) {
                    keys[count++] = String.valueOf((char) code);
                    printChar = printChar.concat(String.valueOf((char) code));
                }
            }
            if(printChar.length()>0 && (printChar.charAt(0) == printChar.charAt(printChar.length()-1) && printChar.charAt(0) == printChar.charAt(printChar.length()/2))) {
//                System.out.println("Character.isLetter(code) && caps" + Character.isLetter(code) +"  " + caps);
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }

//                ic.commitText(String.valueOf(code), 1);

                Log.d("print onkey", (String.valueOf(code)));

//                System.out.println("770 code   "+ code + "  primaryCode  "+ primaryCode);
//                mComposing.append((char) primaryCode);


                if(kv.getKeyboard() == mQwertyKeyboard ) {

                    if(duration>400 && (primaryCode == 113 || primaryCode == 119 || primaryCode == 101 || primaryCode == 114 || primaryCode == 116 || primaryCode == 121 || primaryCode == 117 || primaryCode == 105 || primaryCode == 111 || primaryCode == 112)){
                        if(primaryCode == 113)
                            code = (char)2535;
                        else if(primaryCode == 119)
                            code = (char)2536;
                        else if(primaryCode == 101)
                            code = (char)2537;
                        else if(primaryCode == 114)
                            code = (char)2538;
                        else if(primaryCode == 116)
                            code = (char)2539;
                        else if(primaryCode == 121)
                            code = (char)2540;
                        else if(primaryCode == 117)
                            code = (char)2541;
                        else if(primaryCode == 105)
                            code = (char)2542;
                        else if(primaryCode == 111)
                            code = (char)2543;
                        else if(primaryCode == 112)
                            code = (char)2534;

                        ic.commitText(String.valueOf(code), 1);

                        mComposing_bn_ver.setLength(0);
                        mComposing.setLength(0);
                    }

                    else {
                        mComposing_bn_ver.append(code);
                        //                    Handling h deletetion
//                        System.out.println("laterCharofAKey  " + laterCharofAKey + "   prevPrimaryCodeofCurPref  " + prevPrimaryCodeofCurPref + "  primaryCode  " + primaryCode);
                        //                    if( laterCharofAKey ) {
                        //                        handleLetDelofSugPref(primaryCode, prevPrimaryCodeofCurPref);
                        //                    }
                        char checkUpCase;

                        if (printChar.length() > 0) {
                            if (!caps) {
                                checkUpCase = printChar.charAt(0);
                                //                            if( checkUpCase == 'T' || checkUpCase == 'D' || checkUpCase == 'O' || checkUpCase == 'N' ) {
                                if (upCasehandle(primaryCode)) {
                                    checkUpCase = Character.toUpperCase(checkUpCase);
                                    mComposing.append(checkUpCase);
                                } else mComposing.append(printChar.charAt(0));
                            } else {
                                if (printChar.charAt(0) == 'g' ||printChar.charAt(0) == 'o' || printChar.charAt(0) == 't' || printChar.charAt(0) == 'd' || printChar.charAt(0) == 'z' || printChar.charAt(0) == 'y' || printChar.charAt(0) == 'i' || printChar.charAt(0) == 'u' || printChar.charAt(0) == 's' || printChar.charAt(0) == 'r' || printChar.charAt(0) == 'n' || printChar.charAt(0) == 'h') {
                                    mComposing.append(Character.toUpperCase(printChar.charAt(0)));
                                } else mComposing.append(printChar.charAt(0));

                            }
                        } else {
//                            //                        System.out.println("641 code   "+ code + "  primaryCode  "+ primaryCode);
                            char engofBeng = getEngVersionBengLet(code);
//                            System.out.println("657   code" + code + " engofBeng  " + engofBeng);
                            mComposing.append((String.valueOf(engofBeng)));
                        }
                        //                    delCharCorIcLet.add(1);
                        if (primaryCode - prevPrimaryCodeofCurPref == 1)
                            handleLetterCharectersOfAKeyInBengali(primaryCode);

//                        System.out.println(">>>>>>>mocomposing_bn_length >>> " + mComposing_bn_ver.length() + ">>>mocomp bang>> " + mComposing_bn_ver);
                        if (mComposing_bn_ver.length() > 1) {
                            ic.deleteSurroundingText(mComposing_bn_ver.length() - 1, 0);
                        }

                        PhoneticParser avro = PhoneticParser.getInstance();
                        avro.setLoader(new PhoneticXmlLoader(context));
                        String bangla = avro.parse(mComposing.toString());

//                        System.out.print("func banglish in onkey>> " + mComposing.toString() + "avro bangla in print >>" + bangla + ">> ");
                        String banglaIC = "";

                        for (int g = 0; g < bangla.length(); g++) {
//                            System.out.print((char) getIcValueForBengLetters(bangla.charAt(g)) + " ");
                            ic.commitText(String.valueOf((char) getIcValueForBengLetters(bangla.charAt(g))), 1);
                            banglaIC = banglaIC.concat("" + (char) getIcValueForBengLetters(bangla.charAt(g)));

                        }

//                        System.out.println(">>>>" + banglaIC);
                        mComposing_bn_ver.setLength(0);
                        mComposing_bn_ver.append(banglaIC);

                        //for capsLocked
                        if (!capsLocked && caps) {
                            kv.setShifted(false);
                            caps = !caps;
                        }
                    }
                }
                //for english
                else if(kv.getKeyboard() == mQwertyEngKeyboard ) {

                    if(duration>400){
                        if(primaryCode == 113)
                            code = (char)49;
                        else if(primaryCode == 119)
                            code = (char)50;
                        else if(primaryCode == 101)
                            code = (char)51;
                        else if(primaryCode == 114)
                            code = (char)52;
                        else if(primaryCode == 116)
                            code = (char)53;
                        else if(primaryCode == 121)
                            code = (char)54;
                        else if(primaryCode == 117)
                            code = (char)55;
                        else if(primaryCode == 105)
                            code = (char)56;
                        else if(primaryCode == 111)
                            code = (char)57;
                        else if(primaryCode == 112)
                            code = (char)48;

                        ic.commitText(String.valueOf(code), 1);

                        mComposing_bn_ver.setLength(0);
                        mComposing.setLength(0);
                    }
                    else {
                        ic.commitText(String.valueOf(code), 1);

                        if (caps) {
                            code = Character.toLowerCase(code);
                        }

                        mComposing.append(String.valueOf(code));

                        //for capsLocked
                        if (!capsLocked && caps) {
                            kv.setShifted(false);
                            caps = !caps;
                        }
                    }
                }
                else{

                    if(duration>400){
                        if(primaryCode == 49)
                            code = (char)2535;
                        else if(primaryCode == 50)
                            code = (char)2536;
                        else if(primaryCode == 51)
                            code = (char)2537;
                        else if(primaryCode == 52)
                            code = (char)2538;
                        else if(primaryCode == 53)
                            code = (char)2539;
                        else if(primaryCode == 54)
                            code = (char)2540;
                        else if(primaryCode == 55)
                            code = (char)2541;
                        else if(primaryCode == 56)
                            code = (char)2542;
                        else if(primaryCode == 57)
                            code = (char)2543;
                        else if(primaryCode == 48)
                            code = (char)2534;
                        else if(primaryCode == 2535)
                            code = (char)49;
                        else if(primaryCode == 2536)
                            code = (char)50;
                        else if(primaryCode == 2537)
                            code = (char)51;
                        else if(primaryCode == 2538)
                            code = (char)52;
                        else if(primaryCode == 2539)
                            code = (char)53;
                        else if(primaryCode == 2540)
                            code = (char)54;
                        else if(primaryCode == 2541)
                            code = (char)55;
                        else if(primaryCode == 2542)
                            code = (char)56;
                        else if(primaryCode == 2543)
                            code = (char)57;
                        else if(primaryCode == 2534)
                            code = (char)48;

                        ic.commitText(String.valueOf(code), 1);
                    }
                    else {
                        ic.commitText(String.valueOf(code), 1);
                    }
                }


//                System.out.println("      ~~~~~~~~~ on key mComposing   " + mComposing);
//                System.out.println("      ~~~~~~~~~ on key mComposing   " + mComposing_bn_ver);

                oncePrinted = true;
                prevPrimaryCodeofCurPref = primaryCode;
                isLastWordSwipdeAndAnSpaceTapped = false;
                islastWordSwipe =false;
                isNexWord = false;
//                islastWordSwipe = false;
                if( primaryCode == 32 ) {
//                 System.out.println("710  space   "+ primaryCode);
                }
            }
            else if(printChar.length()==0){
                if(duration>400){
                    if(primaryCode == 49)
                        code = (char)2535;
                    else if(primaryCode == 50)
                        code = (char)2536;
                    else if(primaryCode == 51)
                        code = (char)2537;
                    else if(primaryCode == 52)
                        code = (char)2538;
                    else if(primaryCode == 53)
                        code = (char)2539;
                    else if(primaryCode == 54)
                        code = (char)2540;
                    else if(primaryCode == 55)
                        code = (char)2541;
                    else if(primaryCode == 56)
                        code = (char)2542;
                    else if(primaryCode == 57)
                        code = (char)2543;
                    else if(primaryCode == 48)
                        code = (char)2534;
                    else if(primaryCode == 2535)
                        code = (char)49;
                    else if(primaryCode == 2536)
                        code = (char)50;
                    else if(primaryCode == 2537)
                        code = (char)51;
                    else if(primaryCode == 2538)
                        code = (char)52;
                    else if(primaryCode == 2539)
                        code = (char)53;
                    else if(primaryCode == 2540)
                        code = (char)54;
                    else if(primaryCode == 2541)
                        code = (char)55;
                    else if(primaryCode == 2542)
                        code = (char)56;
                    else if(primaryCode == 2543)
                        code = (char)57;
                    else if(primaryCode == 2534)
                        code = (char)48;

                    System.out.println(">>>>>>>>>>>>>>>long pressss symbol>>>> " + code);

                    ic.commitText(String.valueOf(code), 1);
                }
                else {
                    System.out.println(">>>>>>>>>>>>>>>long pressss symbol bng>>>> " + code);
                    ic.commitText(String.valueOf(code), 1);
                }
            }
        }

//        System.out.println("onKey(int primaryCode, int[] keyCodes) ends");
    }
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    private void handleLetDelofSugPref() {

        if( prevPrimaryCodeofCurPref == 2454 || prevPrimaryCodeofCurPref == 2456
                || prevPrimaryCodeofCurPref == 2459 || prevPrimaryCodeofCurPref == 2461
                || prevPrimaryCodeofCurPref == 2464 || prevPrimaryCodeofCurPref == 2466
                || prevPrimaryCodeofCurPref == 2469 || prevPrimaryCodeofCurPref == 2471
//                || prevPrimaryCodeofCurPref == 2475 || prevPrimaryCodeofCurPref == 2477
                || prevPrimaryCodeofCurPref == 2486 || prevPrimaryCodeofCurPref == 2525 ) {

            if(mComposing.length()>0) {
                mComposing.deleteCharAt( mComposing.length() - 1 );
            }
        }
//        if( (prevPrimaryCode - primaryCode == 1)
//                || ( prevPrimaryCode == 2469 && primaryCode == 2463)
//                || ( prevPrimaryCode == 2471 && primaryCode == 2465)
////                || ( prevPrimaryCode == )
//                ) {
//
//            if(mComposing.length()>0) {
//                mComposing.deleteCharAt( mComposing.length() - 1 );
//            }
//        }
    }

    private void handleLetterCharectersOfAKeyInBengali(int primaryCode) {
        char ch = mComposing.charAt( mComposing.length() - 1 );

        if( primaryCode == 2454 || primaryCode == 2456 ||
                primaryCode == 2459 || primaryCode == 2461 ||
                primaryCode == 2464 || primaryCode == 2466 ||
                primaryCode == 2469 || primaryCode == 2471
                || primaryCode == 2486 || primaryCode == 2525  ) {

            mComposing.append('h');
        }
//        delCharCorIcLet.remove( delCharCorIcLet.size() -1 );
//        delCharCorIcLet.add( 2 );
    }

    private boolean upCasehandle(int primaryCode ) {

//        checkUpCase = Character.toUpperCase(checkUpCase);
        if(primaryCode== 2437 || primaryCode==2440 || primaryCode == 2442 || primaryCode == 2463 || primaryCode == 2464
                || primaryCode == 2465 || primaryCode == 2466 ||  primaryCode == 2467 || primaryCode == 2487
                || primaryCode == 2524) {
            return true;
        }
        else return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Onkeylong presss >>>>>> "+keyCode);
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onPress(int primaryCode) {

//        System.out.println("public void onPress(int primaryCode) starts");
        SharedPreferences prefs = getSharedPreferences("test", MODE_PRIVATE);
        int sound = prefs.getInt("Sound on Tap",0);
        int vibrate = prefs.getInt("Vibrate on Tap",0);
        //  int vibrate = prefs.getInt("Vibrate on Tap",0);
        //   System.out.println("my keyboard     sound   "+sound+" vibrate     "+vibrate);


        if(sound == 1) {

            float vol = (float) 0.5;

            am.playSoundEffect(AudioManager.FX_KEY_CLICK, vol);
        }
        if(vibrate == 1)
        {
            vib.vibrate(50);
        }



        onpress_code = primaryCode;

//        Log.d("onPress", "onPress");
        cur_time_onpress = android.os.SystemClock.elapsedRealtime();

//        System.out.println("ON PRESS  -- cur_time_onpress   -- >  "+ cur_time_onpress);

        printChar = "";


//        if( !fromType ) setCandidatesViewShown(false);

        primaryKeyofOnPress = primaryCode;
        laterCharofAKey =false;
        idxOfEndOfBengSugShow = 998;
//        isSuggestionTapped = false;
//        System.out.println("public void onPress(int primaryCode) ends            "+primaryCode + "    primaryKeyofOnPress   " + primaryKeyofOnPress);

    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private void sendKey(int keyCode) {  //580 main devkeyboard

//        System.out.println("sendKey  starts");

        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
//                if (keyCode >= '0' && keyCode <= '9') {
//                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
//                }
//                else {
//                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
//                }
                break;
        }

//        System.out.println("sendKey  ends");

    }

    private String getWordSeparators() {
//        System.out.println("getWordSeparators  starts with  ret mWordSeparators");
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code) {
//        System.out.println("isWordSeparator  starts with no ret");

        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char)code));


    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {

//        System.out.println("commitTyped  starts");


        if (mComposing.length() > 0) {
//            inputConnection.commitText(mComposing, mComposing.length());
            mComposing.setLength(0);
            mComposing_bn_ver.setLength( 0 );

            delCharCorIcLet.clear();

            if( v_trie.search(mComposing.toString()) ) {
//                System.out.println("commitTyped -->  mComposing.toString() " + mComposing.toString() + " v_trie.search(mComposing.toString())  "+ v_trie.search(mComposing.toString()));
//                System.out.println("commitTyped -->  mComposing_bn_ver.toString() " + mComposing_bn_ver.toString() );
                add_word_to_dictonary( mComposing.toString() );
            }
//            updateCandidates();
        }

//        System.out.println("commitTyped  ends");
    }


    private void handleShift() {

//        System.out.println("handleShift  starts");

        if (kv == null) {
            return;
        }

        Keyboard currentKeyboard = kv.getKeyboard();
        if (mQwertyEngKeyboard == currentKeyboard || mQwertyKeyboard == currentKeyboard) {
            // Alphabet keyboard
//            checkToggleCapsLock();
            kv.setShifted(!kv.isShifted());
            caps = !caps;
        } else if (currentKeyboard == mSymbolsKeyboard) {
            mSymbolsKeyboard.setShifted(true);
            setLatinKeyboard(mSymbolsShiftedKeyboard);
            mSymbolsShiftedKeyboard.setShifted(true);

            lastKeyboardSymbol = mSymbolsKeyboard;

            mCandidateView.setSuggestions(emojis, true, true);
            setCandidatesViewShown(true);
        } else if (currentKeyboard == mSymbolsBngKeyboard) {
            mSymbolsBngKeyboard.setShifted(true);
            setLatinKeyboard(mSymbolsShiftedKeyboard);
            mSymbolsShiftedKeyboard.setShifted(true);

            lastKeyboardSymbol = mSymbolsBngKeyboard;

            mCandidateView.setSuggestions(emojis, true, true);
            setCandidatesViewShown(true);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mSymbolsShiftedKeyboard.setShifted(false);
            if(lastKeyboardSymbol == mSymbolsKeyboard) {
                setLatinKeyboard(mSymbolsKeyboard);
                mSymbolsKeyboard.setShifted(false);
            }
            else if(lastKeyboardSymbol == mSymbolsBngKeyboard){
                setLatinKeyboard(mSymbolsBngKeyboard);
                mSymbolsBngKeyboard.setShifted(false);
            }

            mCandidateView.setSuggestions(emojis, true, true);
            setCandidatesViewShown(true);
        } else if(currentKeyboard == mProvatKeyboard){
            mProvatKeyboard.setShifted(true);
            setLatinKeyboard(mProvatKeyboardShift);
            mProvatKeyboardShift.setShifted(true);

        } else if(currentKeyboard == mProvatKeyboardShift){
            mProvatKeyboardShift.setShifted(false);
            setLatinKeyboard(mProvatKeyboard);
            mProvatKeyboard.setShifted(false);

        }

//        System.out.println("handleShift  end");

    }

    private void checkToggleCapsLock() {
//        System.out.println("checkToggleCapsLock  starts");

        long now = System.currentTimeMillis();
        mCapsLock = !mCapsLock;
//        if (mLastShiftTime + 800 > now) {
//            mCapsLock = !mCapsLock;
//            mLastShiftTime = 0;
//        } else {
//            mLastShiftTime = now;
//        }

//        System.out.println("checkToggleCapsLock  ends");
    }

    private IBinder getToken() {
//        System.out.println("IBinder getToken  starts with no return");

        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    private void setLatinKeyboard(CustomKeyboard nextKeyboard) {

//        System.out.println("setLatinKeyboard   Starts");
//        final boolean shouldSupportLanguageSwitchKey = mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
//        nextKeyboard.setLanguageSwitchKeyVisibility(shouldSupportLanguageSwitchKey);
        kv.setKeyboard(nextKeyboard);
        mCurKeyboard = nextKeyboard;
        if(nextKeyboard == mQwertyKeyboard || nextKeyboard == mQwertyEngKeyboard)
            kv.gesture_color = true;
        else
            kv.gesture_color = false;
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);


//        System.out.println("setLatinKeyboard   ends");

    }


    @Override
    public View onCreateCandidatesView() {
//        System.out.println(" @@@@@@@####### ------ onCreateCandidatesView ---------######@@@@@@");
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);

//        System.out.println("onCreateCandidatesView   Ends");
        return mCandidateView;
    }

    @Override
    public void onRelease(int primaryCode) {

        popup.dismiss();

        Log.d("onRelease", "onRelease");

        cur_time_onrelease = android.os.SystemClock.elapsedRealtime();  //Returns milliseconds since boot, including time spent in sleep.

//        System.out.println("ON Release  -- eventDuration   -- >  "+ cur_time_onrelease);

        word_duration = cur_time_onrelease - cur_time_onpress;

        double word_duration_db =  (double)word_duration/1000;

//        System.out.println("ON Release  -- word_duration   -- >  "+ word_duration+ "ms   in double: " + word_duration_db+ "s ");



        Log.d("----------runNo", "-----"+wordrun++);
//        System.out.println("primaryCode    onrelease    "+primaryCode);
//        Log.d("array", "new");
        //Log.d("count", "" + count);
//        String tpSt;
//        String printLetterCount="", showLetterlist="";
//        String pastCount="", showLetter=" a  b  c  d  e  f  g  h  i  j  k  l  m  n  o  p  q  r  s  t  u  v  w  x  y  z";
//
//        tpSt = ""+String.valueOf( a );

//        int val_a = 'a';
//
//        for(int i=1; i<27; i++) {
//
//            showLetterlist = showLetterlist.concat( (""+ ( (char) (val_a+i-1) ) ) + "= "+String.valueOf( inarr[ i ] )+","  );
//            printLetterCount = printLetterCount.concat( String.valueOf( inarr[ i ] )+" " );
//            sumOfinarr += inarr[ i ];
//        }

        String cntCheck = String.valueOf( count );
        cntCheck = cntCheck.concat("  sumOfinarr ");
        cntCheck = cntCheck.concat( String.valueOf(sumOfinarr) );

        Log.d("----------", "-----------------");
        //Log.d("a::::", ""+((char) (98+i)) );
        Log.d("count = ", cntCheck);
        Log.d("kyestring printchar:"+String.valueOf(printChar.length()), " "+printChar);



        int lenPrintChar = printChar.length();
//        int sameLettercnt = 0, printBaseValue = 7, idans=1;
//        char[] printedLettersCharArray = printChar.toCharArray();
//        char[] ansStringCharArr = new char[ 100 ];
        Keyboard currentKeyboard = kv.getKeyboard();
        mCurKeyboard = (CustomKeyboard) currentKeyboard;
        if((currentKeyboard == mQwertyKeyboard || currentKeyboard == mQwertyEngKeyboard || currentKeyboard == mProvatKeyboard || currentKeyboard == mProvatKeyboardShift) && primaryCode!=32 ) {

            String ansString="", suggestion_prefix="";
            List<String> possible_words;
            if(printChar.length() > 1) {
                possible_words = detctWordFromGesturePattern(printChar);
                if(possible_words.size() > 0)ansString = possible_words.get(0);
                if(possible_words.size() > 1)suggestion_prefix = possible_words.get(1);
                else suggestion_prefix = ansString;

            }
            else possible_words = null;
//        if(printChar.length() > 0) {
//
//            ansString =  ansString.concat(""+printedLettersCharArray[0]);
//
//            for(int i=0; i<lenPrintChar; i++) {
//
//                sameLettercnt = 0;
//
//                for(j=i+1; j<lenPrintChar; j++) {
//
//                    if( printedLettersCharArray[i] == printedLettersCharArray[ j ] ) {
//
//                        sameLettercnt++;
//                    }
//                    else break;
//                }
//
//                if( i>0 && sameLettercnt >= printBaseValue ) {
//                    //printCharinScreen( printedLetters[ i ] );
//                    ansStringCharArr[ idans++ ] = printedLettersCharArray[ i ];
//                    ansString = ansString.concat(""+printedLettersCharArray[i] );
//                }
//
//                i = j-1;
//            }
//        }
//       /* Log.d("kyestring:", ""+keys );*/
//        //Log.d("kyestring printchar:", ""+printChar);
//        /*for(int key=0;key<count;key++){
//            Log.d("keys asdflkjaslkdf", ""+keys[key]);
//        }*/

            /////////////////////////////////// For Data COllection ////////////////
            myData = "";
            myData = myData + "Gesture(printChar): " + printChar+ " lenPrintChar: " + lenPrintChar + " "
                    + " ansString: " + ansString + " word_duration_db: " + word_duration_db+" sec " + "";

            Log.d("ansString: ", ansString);

            suggestionWord = ansString;
////        System.out.println("Search Result amra " + v_trie.search("amra"));


//            if(oncePrinted && ansString.length() == 1 ) {
            if(oncePrinted ) {
                oncePrinted = false;
                suggestion_prefix = mComposing.toString();

                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + mComposing.toString());

                if(currentKeyboard == mQwertyEngKeyboard){
                    suggIcWord = mComposing.toString();
                }
                else
                    suggIcWord = mComposing_bn_ver.toString();

                fromType = true;
                islastWordSwipe = false;

//                System.out.println(" Asche suggestion_prefix   "+suggestion_prefix + "  mComposing   "+ mComposing.toString() );
//                System.out.println("------------  1028     space   "+ primaryCode);
            }
            else if(printChar.length() > 0 ) {
                myData = myData + " BanglaOnScreenWord: ";

                if( islastWordSwipe ) ic.commitText( String.valueOf( (char) 32 ), 1 );
//                System.out.println("suggicword before>> "+suggIcWord);

                printCharinScreen(ansString);
//                System.out.println("suggicword after>> "+suggIcWord);
                fromType=false;
                islastWordSwipe = true;
                lastWrittenWordWithSwipe = suggIcWord;

////                System.out.println("   1119 ---> current cursor position or chararcters before cursor" + getCurrentInputEditorInfo().);
//                mComposing_bn_ver.setLength(0);
//                mComposing_bn_ver.append( suggIcWord );
            }

//            System.out.println("Search Result " + suggIcWord + " ->    " + mComposing_bn_ver.toString() );
            System.out.println("Search Result " + suggestion_prefix + " -> " + v_trie.search(suggestion_prefix));


            if(currentKeyboard == mQwertyKeyboard || currentKeyboard == mProvatKeyboard || currentKeyboard == mProvatKeyboardShift) {

                //for caps in word search for suggestion
                if(v_trie.search(suggestion_prefix) == false){
                    String new_prefix = "";
                    boolean flag = false;
                    int in=-1;
                    while(in<suggestion_prefix.length()) {
                        String temp = "";
                        flag = false;
                        for (int i = 0; i < suggestion_prefix.length(); i++) {
                            if (flag == false && i > in) {
                                if (suggestion_prefix.charAt(i) == 't' || suggestion_prefix.charAt(i) == 'd' || suggestion_prefix.charAt(i) == 'n' || suggestion_prefix.charAt(i) == 'o') {
                                    temp += Character.toUpperCase(suggestion_prefix.charAt(i));
                                    in = i;
                                    flag = true;
                                } else
                                    temp += suggestion_prefix.charAt(i);
                            } else
                                temp += suggestion_prefix.charAt(i);
                        }
                        if(flag == false){
                            break;
                        }
                        if (v_trie.search(temp) == true && flag == true) {
                            new_prefix += temp;
                            break;
                        }
                    }
                    if (v_trie.search(new_prefix) == true && flag == true) {
                        v_trie.wordSuggest(new_prefix);
                    }
                }

                v_trie.wordSuggest(suggestion_prefix);
            }
            // for english
            else
                e_trie.wordSuggest(suggestion_prefix);

            if(possible_words!= null && possible_words.size() >2) {

                if(currentKeyboard == mQwertyKeyboard || currentKeyboard == mProvatKeyboard || currentKeyboard == mProvatKeyboardShift){
                    for(int i=2; i<=idxOfBengSugEndStList; i++) {
//                        System.out.println(i + " Bengali possible_words.get(i)  " + possible_words.get(i));
                        printCharInConsoleRun(context, possible_words.get(i) );
                    }
                    idxOfEndOfBengSugShow = totalSuggestedWOrdsCount;
                    for(int i=idxOfBengSugEndStList+1; i<possible_words.size(); i++) {
//                        System.out.println(i + "English possible_words.get(i)  " + possible_words.get(i));
                        allSuggestedWOrds[totalSuggestedWOrdsCount++] = possible_words.get(i);
                    }
                }
                //for english
                else{
                    for(int i=2; i<possible_words.size();i++){
//                        System.out.println(i + "English possible_words.get(i)  " + possible_words.get(i));
                        allSuggestedWOrdsInEng[totalSuggestedWOrdsCount] = possible_words.get(i);
                        allSuggestedWOrds[totalSuggestedWOrdsCount++] = possible_words.get(i);
                    }
                }
            }


//            System.out.println("Total all Suggested Words: where    idxOfEndOfBengSugShow   " + idxOfEndOfBengSugShow);
            String tpSugLineShow = "";
            List<String> listSuggestedWords = new ArrayList<String>();
            for(int i=0; i<totalSuggestedWOrdsCount; i++) {
//        /*System.out.print("  " + allSuggestedWOrds[ i ]);*/
                if(currentKeyboard == mQwertyKeyboard || currentKeyboard == mProvatKeyboard || currentKeyboard == mProvatKeyboardShift) {
                    tpSugLineShow = tpSugLineShow.concat(allSuggestedWOrds[i] + " ");
                    listSuggestedWords.add(allSuggestedWOrds[i]);
                }
                //for english
                else{
                    tpSugLineShow = tpSugLineShow.concat(allSuggestedWOrdsInEng[ i ]+" ");
                    listSuggestedWords.add(allSuggestedWOrdsInEng[ i ]);
                }
            }
//            System.out.println(tpSugLineShow);

            isSuggestionTapped = false;

            setCandidatesViewShown(true);
            isNexWord = false;
            if(listSuggestedWords.size()>0) {
                mCandidateView.setSuggestions(listSuggestedWords, true, true);
                sugg_flag = false;
            }
            else{
                mCandidateView.setSuggestions(emojis, true, true);
                setCandidatesViewShown(true);
                sugg_flag = true;
            }


        /*e_trie.wordSuggest("al");
        v_trie.wordSuggest("am");*/
            /////////////////////////////////// For Data COllection ////////////////
            ////--------------------Writing words----------------------

//        try {
//
//            myExternalFile = new File(getExternalFilesDir(filepath), filename);
//
//            FileOutputStream fos = new FileOutputStream(myExternalFile, true);
//            String st_write = myData;
//            st_write = st_write.concat("\n");
//
//            fos.write(st_write.getBytes());
//
////            System.out.println("st_write" + st_write );
//
//            fos.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//            write_dc_information();

        }



        //------------------------------------
        /////////////////////////////////// For Data COllection ////////////////
        count = q = w = e = r = t = y = u = i = o = p = a = s = d = f = g = h = j = k = l = z = x = c = v = b = n = m = 0;
        oncePrinted = false;
        for(int i=0; i<30; i++) inarr[ i ] = 0;
//        for(int i=0; i<100; i++) allSuggestedWOrds[i] = "";
        //for(int i=0; i<100; i++) allSuggestedWOrdsInEng[i] = "";
        totalSuggestedWOrdsCount=0;
        sumOfinarr = 0;
        printChar = "";
        letterCount="";
//        System.out.println(">>>>>>>>>>>                            onrealese ends                                      <<<<<<<<<<<<<<<");
    }

    public void write_dc_information() {
        /////////////////////////////////// For Data COllection ////////////////
//        System.out.println("write_dc_information ---- write_dc_information");
        ////--------------------Writing words----------------------
//        try {
////            myExternalFile = new File(getExternalFilesDir(filepath), filename);
//
//            String st_write = myData;
////            st_write = st_write.concat("\n");
//            FileOutputStream f = new FileOutputStream(myExternalFile+"/"+filename, true);
//            PrintWriter pw = new PrintWriter(f);
//            pw.println(st_write);
//            pw.flush();
//            pw.close();
//            f.close();
////            System.out.println("st_write" + st_write );
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        System.out.println("write_dc_information ---- write_dc_information ends");

        //------------------------------------
        /////////////////////////////////// For Data COllection ////////////////
    }

    /**
     * Adding new word to the database of our keyboard.
     * */

    private void add_word_to_dictonary(String add_new_word) {
        ///////////////////////////////////  ////////////////
//        System.out.println("add_word_to_dictonary ---- add_word_to_dictonary  starts");
        ////--------------------Writing words----------------------
//        try {
////            myExternalFile = new File(getExternalFilesDir(filepath), filename);
//
//            String st_write = add_new_word;
////            st_write = st_write.concat("\n");
//            //            this.getResources().openRawResource(R.raw.test1)
//            FileOutputStream f = new FileOutputStream(myExternalFile+"/"+filename, true);
//            PrintWriter pw = new PrintWriter(f);
//            pw.println(st_write);
//            pw.flush();
//            pw.close();
//            f.close();
////            System.out.println("st_write" + st_write );
//
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        System.out.println("add_word_to_dictonary ---- add_word_to_dictonary ends");

        //------------------------------------
    }

    public List<String> detctWordFromGesturePattern(String  gesture_input) {
        List<TrieTraverseNode> trietrav_list =new ArrayList<TrieTraverseNode>();
        List<TrieTraverseNode> trietrav_list_eng =new ArrayList<TrieTraverseNode>(); // eng suggestion
        int gi=0;
        int gesture_len = gesture_input.length();
        char[] gesture_input_CharArray = gesture_input.toCharArray();
        //insert the nearest letters at first in the list
        String stNearestLetters = nearestLetter( gesture_input_CharArray[0] );
        stNearestLetters = stNearestLetters.concat( ""+gesture_input_CharArray[0] );
//        System.out.println(">>>detectwordfromgesture>>> "+"stnearestletters>> "+stNearestLetters);
//        System.out.println(">>>detectwordfromgesture>>> "+"gestureinputchar[0]>> "+gesture_input_CharArray[0]);

        // map to ensure no duplicate string
        HashMap<String, Integer> possible_words_once = new HashMap<>();
        HashMap<String, Integer> possible_words_once_eng = new HashMap<>(); //english suggestion

        int cntFirst = 0;
//        cntFirst = 1;
        while( cntFirst<gesture_len && gesture_input_CharArray[ cntFirst ] == gesture_input_CharArray[ 0 ] ) { cntFirst++;}
//        System.out.println(">>>detectwordfromgesture>>> "+"cntfirst>> "+cntFirst);


        for(int i=0; i<stNearestLetters.length(); i++) {

//            System.out.println(">>>detectwordfromgesture>>> "+"stnearestletter>> "+stNearestLetters.charAt(i));

            TrieTraverseNode ttvnd_sec = new TrieTraverseNode();
            TrieTraverseNode ttvnd_sec_upcase = new TrieTraverseNode();

            TrieTraverseNode ttvnd_sec_eng = new TrieTraverseNode(); //english suggestion

            ttvnd_sec.cur_node = v_trie.getRootNode();

            ttvnd_sec_eng.cur_node = e_trie.getRootNode();
            //add to trie
            //add char then find the node and add the node
            TrieNode next_node =  v_trie.isAWord_treeTrav(ttvnd_sec.cur_node, stNearestLetters.charAt( i ));
            TrieNode next_node_eng =  e_trie.isAWord_treeTrav(ttvnd_sec_eng.cur_node, stNearestLetters.charAt( i ));  //english suggestion

            if( next_node != null ) {
//                System.out.println(">>>detectwordfromgesture>>> "+"next_node != null");
                ttvnd_sec.cur_node = next_node;
                ttvnd_sec.cur_word  =  ttvnd_sec.cur_word.concat( ""+stNearestLetters.charAt( i ) );

                if(stNearestLetters.charAt(i) == gesture_input_CharArray[ 0 ] ) ttvnd_sec.score = ttvnd_sec.score + (double) cntFirst;
                else ttvnd_sec.score = ttvnd_sec.score + (double) (cntFirst/2.0);

//                System.out.println(">>>detectwordfromgesture>>> "+"score>> "+ttvnd_sec.score+"word>> "+ttvnd_sec.cur_word);

                if( possible_words_once.get( ttvnd_sec.cur_word ) == null ) {
                    ttvnd_sec.flag = v_trie.search(ttvnd_sec.cur_word);
                    trietrav_list.add(ttvnd_sec);

////                    System.out.println( ttvnd_sec.cur_word + " "+  possible_words_once.get( ttvnd_sec.cur_word ) );
                    possible_words_once.put(ttvnd_sec.cur_word,  trietrav_list.size()-1);
                }
            }

            //ট , ড, ণ, অ
            char checkUpCase = Character.toUpperCase( stNearestLetters.charAt( i ) );
            if( checkUpCase == 'T' || checkUpCase == 'D' || checkUpCase == 'O' || checkUpCase == 'N' || checkUpCase == 'S' ||
                    checkUpCase == 'R' || checkUpCase == 'I' || checkUpCase == 'U') {

                ttvnd_sec_upcase.cur_node = v_trie.getRootNode(); // root noede

                TrieNode next_node_upcase =  v_trie.isAWord_treeTrav(ttvnd_sec_upcase.cur_node, checkUpCase );
//                TrieNode next_node_eng_upcase =  e_trie.isAWord_treeTrav(ttvnd_sec_eng.cur_node, stNearestLetters.charAt( i ));  //english suggestion

                if( next_node_upcase != null ) {
//                    System.out.println(">>>detectwordfromgesture>>> "+"Upcase next_node != null");
                    ttvnd_sec_upcase.cur_node = next_node_upcase;
                    ttvnd_sec_upcase.cur_word  =  ttvnd_sec_upcase.cur_word.concat( ""+checkUpCase );

                    if( stNearestLetters.charAt(i) == gesture_input_CharArray[ 0 ] ) ttvnd_sec_upcase.score = ttvnd_sec_upcase.score + (double) cntFirst;
                    else ttvnd_sec_upcase.score = ttvnd_sec_upcase.score + (double) (cntFirst/2.0);

//                    System.out.println(">>>detectwordfromgesture>>> "+"Upcase score>> "+ttvnd_sec_upcase.score+"word>> "+ttvnd_sec_upcase.cur_word);

                    ttvnd_sec_upcase.flag = v_trie.search(ttvnd_sec_upcase.cur_word);
                    trietrav_list.add( ttvnd_sec_upcase );

                    if( possible_words_once.get( ttvnd_sec_upcase.cur_word ) == null ) {

////                    System.out.println( ttvnd_sec.cur_word + " "+  possible_words_once.get( ttvnd_sec.cur_word ) );
                        possible_words_once.put( ttvnd_sec_upcase.cur_word,  trietrav_list.size()-1);
                    }
                }

            }


            //english suggestion
            if( next_node_eng != null ) {
                ttvnd_sec_eng.cur_node = next_node_eng;
                ttvnd_sec_eng.cur_word  =  ttvnd_sec_eng.cur_word.concat( ""+stNearestLetters.charAt( i ) );

                if(stNearestLetters.charAt(i) == gesture_input_CharArray[ 0 ] ) ttvnd_sec_eng.score = ttvnd_sec_eng.score + (double) cntFirst;
                else ttvnd_sec_eng.score = ttvnd_sec_eng.score + (double) (cntFirst/2.0);

                ttvnd_sec_eng.flag = e_trie.search(ttvnd_sec_eng.cur_word);
                trietrav_list_eng.add(ttvnd_sec_eng);

                if( possible_words_once_eng.get( ttvnd_sec_eng.cur_word ) == null ) {

////                    System.out.println( ttvnd_sec.cur_word + " "+  possible_words_once.get( ttvnd_sec.cur_word ) );
                    possible_words_once_eng.put(ttvnd_sec_eng.cur_word,  trietrav_list_eng.size()-1);
                }
            }

        }

////        System.out.println("cntFirst   " + cntFirst);
        /*Checking the condition of the list afrter first letter*/
//        for(int i=0; i<trietrav_list.size(); i++) {
//            TrieTraverseNode ttvnd = trietrav_list.get(i);
////            System.out.println("After first letter i "+i+"  word->  "+ttvnd.cur_word+"  score = "+ttvnd.score + " "+ v_trie.search( ttvnd.cur_word ) );
//        }

        gi = cntFirst;

////        System.out.println("gi   " + gi+"  gesture_input_CharArray[gi]   "+gesture_input_CharArray[gi]);

        while( gi<gesture_len ) {
////            System.out.println("109 gi<gesturelen");
            int cur_score = 0;
            int tp_gi = gi;
            //string to char array transfer first

            while(tp_gi<gesture_len && gesture_input_CharArray[ tp_gi ] == gesture_input_CharArray[ gi ] )
            {
                tp_gi++;
                cur_score++;
            }
            boolean firstLetterAlreadyinserted = false;

            //nearest letters
//            stNearestLetters = "";
            stNearestLetters = nearestLetter( gesture_input_CharArray[gi] );
            stNearestLetters = stNearestLetters.concat( ""+gesture_input_CharArray[gi] );

//            System.out.println(">>>detectwordfromgesture>>> "+"stnearestletters>> "+stNearestLetters);

            int curNearest = stNearestLetters.length();
            //ট , ড, ণ, অ
            for(int j=0; j< curNearest ; j++) {

                char checkUpCase = Character.toUpperCase( stNearestLetters.charAt( j ) );
                if( checkUpCase == 'T' || checkUpCase == 'D' || checkUpCase == 'O' || checkUpCase == 'N' || checkUpCase == 'S' ||
                        checkUpCase == 'R' || checkUpCase == 'I' || checkUpCase == 'U') {
                    stNearestLetters = stNearestLetters.concat( ""+checkUpCase );
                }
            }

            int sz_tri_trav = trietrav_list.size();


            // for bengali works fine
            for(int i=0; i<sz_tri_trav; i++) {
////                System.out.println("126 trietrav");
                TrieTraverseNode ttvnd = trietrav_list.get(i);
                //add char then find the node and add the node
////                if(ttvnd.cur_word.charAt(0) == 'a') System.out.println("1--> i = "+i+" cur_word = "+ttvnd.cur_word+" cur_char = "+ gesture_input_CharArray[gi]);
                //for all nearest characters
                for(int j=0; j<stNearestLetters.length(); j++) {
                    //add to trie
                    //add char then find the node and add the node
                    char ch_cur = stNearestLetters.charAt( j );
                    TrieNode next_node =  v_trie.isAWord_treeTrav(ttvnd.cur_node, ch_cur);
                    String new_word = ttvnd.cur_word.concat( ""+ch_cur );

                    if( possible_words_once.get( new_word ) == null && next_node != null)
                    {
//                        //                    System.out.println("nex_node is not null");
                        TrieTraverseNode ttvnd_third = new TrieTraverseNode();

                        ttvnd_third.cur_node = next_node;
                        ttvnd_third.cur_word  =  ttvnd.cur_word.concat( ""+ch_cur );
//                        ttvnd_third.score = ttvnd.score + (double) cur_score;

                        if(stNearestLetters.charAt( j ) == gesture_input_CharArray[ gi ] ||  Character.toLowerCase( stNearestLetters.charAt( j ) ) == gesture_input_CharArray[ gi ] ) {
                            ttvnd_third.score = ttvnd.score + (double) cur_score;
                        }
                        else ttvnd_third.score = ttvnd.score + (double) (cur_score/2.0);

                        trietrav_list.add( ttvnd_third );

                        possible_words_once.put(ttvnd_third.cur_word, trietrav_list.size()-1);

                    }
                    else if( possible_words_once.get( new_word ) != null && next_node != null) {

                        int id_of_tt_list = possible_words_once.get( new_word  );
                        TrieTraverseNode ttvnd_edit_score = trietrav_list.get( id_of_tt_list );

                        if(  (  stNearestLetters.charAt( j ) == gesture_input_CharArray[ gi ]
                                || Character.toLowerCase( stNearestLetters.charAt( j ) ) == gesture_input_CharArray[ gi ]
                        )
                                && ttvnd_edit_score.score < ( ttvnd.score + (double) cur_score) )
                        {

                            ttvnd_edit_score.score = ttvnd.score + (double) cur_score;
                        }
                        else if(  ( stNearestLetters.charAt( j ) != gesture_input_CharArray[ gi ]
                                || Character.toLowerCase( stNearestLetters.charAt( j ) ) != gesture_input_CharArray[ gi ]
                        )
                                && ttvnd_edit_score.score < ( ttvnd.score + (double) (cur_score/2.0) ) )
                        {
                            ttvnd_edit_score.score = ttvnd.score + (double) (cur_score/2.0);
                        }
                    }
                    new_word =  ""+ch_cur;

                    if( possible_words_once.get( new_word  ) == null ) {
                        TrieTraverseNode ttvnd_sec = new TrieTraverseNode();
                        ttvnd_sec.cur_node = v_trie.getRootNode();
                        //add to trie
                        //add char then find the node and add the node
                        next_node =  v_trie.isAWord_treeTrav(ttvnd_sec.cur_node, ch_cur);

                        if( next_node != null ) {
                            ttvnd_sec.cur_node = next_node;
                            ttvnd_sec.cur_word  =  ttvnd_sec.cur_word.concat( ""+ch_cur );
//                            ttvnd_sec.score = ttvnd_sec.score + (double) cur_score;

                            if(stNearestLetters.charAt( j ) == gesture_input_CharArray[ gi ]
                                    || Character.toLowerCase( stNearestLetters.charAt( j ) ) == gesture_input_CharArray[ gi ] )
                            {
                                ttvnd_sec.score = ttvnd_sec.score + (double) cur_score;
                            }
                            else ttvnd_sec.score = ttvnd_sec.score + (double) (cur_score/2.0);

                            trietrav_list.add(ttvnd_sec);

                            possible_words_once.put( ttvnd_sec.cur_word, trietrav_list.size()-1 );
                        }
                    }
                    else {
                        int id_of_tt_list = possible_words_once.get( new_word  );
                        TrieTraverseNode ttvnd_edit_score = trietrav_list.get( id_of_tt_list );

                        if( ( stNearestLetters.charAt( j ) == gesture_input_CharArray[ gi ] || Character.toLowerCase( stNearestLetters.charAt( j ) ) == gesture_input_CharArray[ gi ] )
                                && ttvnd_edit_score.score < (double) cur_score)
                        {
                            ttvnd_edit_score.score = (double) cur_score;
                        }
                        else if( (stNearestLetters.charAt( j ) != gesture_input_CharArray[ gi ]
                                || Character.toLowerCase( stNearestLetters.charAt( j ) ) == gesture_input_CharArray[ gi ] )
                                && ttvnd_edit_score.score < (double) (cur_score/2.0) )
                        {
                            ttvnd_edit_score.score = (double) (cur_score/2.0);
                        }
                    }

                }
////                System.out.println("i = "+i+" cur_word = "+ttvnd.cur_word+" cur_char = "+ gesture_input_CharArray[gi]);
            }

            /////////////////////// English (Bi lingual suggestion starts in here ///////////////////////////
            /***
             * *for english swipe suggestion
             */
            int sz_tri_trav_eng = trietrav_list_eng.size();
            for(int i=0; i<sz_tri_trav_eng; i++) {
////                System.out.println("126 trietrav");
                TrieTraverseNode ttvnd_eng = trietrav_list_eng.get(i);
                //add char then find the node and add the node
////                if(ttvnd.cur_word.charAt(0) == 'a') System.out.println("1--> i = "+i+" cur_word = "+ttvnd.cur_word+" cur_char = "+ gesture_input_CharArray[gi]);
                //for all nearest characters

                //length tthik kora jay curNearest
                for(int j=0; j<stNearestLetters.length(); j++) {
                    //add to trie
                    //add char then find the node and add the node
                    char ch_cur = stNearestLetters.charAt( j );
                    TrieNode next_node_eng =  e_trie.isAWord_treeTrav(ttvnd_eng.cur_node, ch_cur);
                    String new_word_eng = ttvnd_eng.cur_word.concat( ""+ch_cur );

                    if( possible_words_once_eng.get( new_word_eng ) == null && next_node_eng != null)
                    {
//                        //                    System.out.println("nex_node is not null");
                        TrieTraverseNode ttvnd_third_eng = new TrieTraverseNode();

                        ttvnd_third_eng.cur_node = next_node_eng;
                        ttvnd_third_eng.cur_word  =  ttvnd_eng.cur_word.concat( ""+ch_cur );
//                        ttvnd_third.score = ttvnd.score + (double) cur_score;

                        if(stNearestLetters.charAt( j ) == gesture_input_CharArray[ gi ] ) ttvnd_third_eng.score = ttvnd_eng.score + (double) cur_score;
                        else ttvnd_third_eng.score = ttvnd_eng.score + (double) (cur_score/2.0);

                        trietrav_list_eng.add( ttvnd_third_eng );

                        possible_words_once_eng.put(ttvnd_third_eng.cur_word, trietrav_list_eng.size()-1);

                    }
                    else if( possible_words_once_eng.get( new_word_eng ) != null && next_node_eng != null){

                        int id_of_tt_list_eng = possible_words_once_eng.get( new_word_eng  );
                        TrieTraverseNode ttvnd_edit_score_eng = trietrav_list_eng.get( id_of_tt_list_eng );

                        if(stNearestLetters.charAt( j ) == gesture_input_CharArray[ gi ] && ttvnd_edit_score_eng.score < ( ttvnd_eng.score + (double) cur_score) ) {
                            ttvnd_edit_score_eng.score = ttvnd_eng.score + (double) cur_score;
                        }
                        else if( stNearestLetters.charAt( j ) != gesture_input_CharArray[ gi ] && ttvnd_edit_score_eng.score < ( ttvnd_eng.score + (cur_score/2.0) ) ) {
                            ttvnd_edit_score_eng.score = ttvnd_eng.score + (double) (cur_score/2.0);
                        }
                    }
                    new_word_eng =  ""+ch_cur;

                    if( possible_words_once_eng.get( new_word_eng  ) == null ) {
                        TrieTraverseNode ttvnd_sec_eng = new TrieTraverseNode();
                        ttvnd_sec_eng.cur_node = e_trie.getRootNode();
                        //add to trie
                        //add char then find the node and add the node
                        next_node_eng =  e_trie.isAWord_treeTrav(ttvnd_sec_eng.cur_node, ch_cur);

                        if( next_node_eng != null ) {
                            ttvnd_sec_eng.cur_node = next_node_eng;
                            ttvnd_sec_eng.cur_word  =  ttvnd_sec_eng.cur_word.concat( ""+ch_cur );
//                            ttvnd_sec.score = ttvnd_sec.score + (double) cur_score;

                            if(stNearestLetters.charAt( j ) == gesture_input_CharArray[ gi ] ) ttvnd_sec_eng.score = ttvnd_sec_eng.score + (double) cur_score;
                            else ttvnd_sec_eng.score = ttvnd_sec_eng.score + (double) (cur_score/2.0);

                            trietrav_list_eng.add(ttvnd_sec_eng);

                            possible_words_once_eng.put( ttvnd_sec_eng.cur_word, trietrav_list_eng.size()-1 );
                        }
                    }
                    else {
                        int id_of_tt_list_eng = possible_words_once_eng.get( new_word_eng  );
                        TrieTraverseNode ttvnd_edit_score_eng = trietrav_list_eng.get( id_of_tt_list_eng );

                        if(stNearestLetters.charAt( j ) == gesture_input_CharArray[ gi ] && ttvnd_edit_score_eng.score < (double) cur_score) {
                            ttvnd_edit_score_eng.score = (double) cur_score;
                        }
                        else if( stNearestLetters.charAt( j ) != gesture_input_CharArray[ gi ] && ttvnd_edit_score_eng.score < (double) (cur_score/2.0) ) {
                            ttvnd_edit_score_eng.score = (double) (cur_score/2.0);
                        }
                    }

                }
////                System.out.println("i = "+i+" cur_word = "+ttvnd.cur_word+" cur_char = "+ gesture_input_CharArray[gi]);
            }

            /////////////////////// English (Bi lingual suggestion ends in here ///////////////////////////



            gi = tp_gi;
        }

        List<String> possibleWordList = new ArrayList<>();
        List<String> possibleWordList_eng = new ArrayList<>(); // for english
        int cnt_found_one = 0, cnt_found_one_eng = 0;

        try {
            if(trietrav_list.size() > 1 ) Collections.sort(trietrav_list);
        }
        catch (Exception e) {
//            System.out.println(e);
        }
        //////////////  for English
        try {
            if(trietrav_list_eng.size() > 1 ) Collections.sort(trietrav_list_eng);
        }
        catch (Exception e) {
//            System.out.println("Eng  "+ e);
        }
        ///////////////

//        for(int i=0; i<trietrav_list.size() && i<2; i++) {
        for(int i=0; i<trietrav_list.size(); i++) {
            TrieTraverseNode ttvnd = trietrav_list.get(i);

//            System.out.println("i "+i+"  word->  "+ttvnd.cur_word+"  score = "+ttvnd.score + " "+ v_trie.search( ttvnd.cur_word ) );
//            if(i < 2) possibleWordList.add(ttvnd.cur_word);
            if( v_trie.search( ttvnd.cur_word ) && ttvnd.cur_word.length() > 1 ) {
                possibleWordList.add(ttvnd.cur_word);
////                System.out.println("i "+i+"  word->  "+ttvnd.cur_word+"  score = "+ttvnd.score + " "+ t.search( ttvnd.cur_word ) );
//                System.out.println("        above word        ------ >>   found in the db ----<<<  "+ttvnd.cur_word + "  score = "+ttvnd.score);
//                possibleWordList.add(ttvnd.cur_word);
                cnt_found_one++;

                if(cnt_found_one > 4 ) break;
            }
//            else {
//                trietrav_list.remove(i);
//                i--;
//            }
        }

        //////////////////////// for English
        for(int i=0; i<trietrav_list_eng.size(); i++) {
            TrieTraverseNode ttvnd_eng = trietrav_list_eng.get(i);

//            System.out.println("ENG ---> i "+i+"  word->  "+ttvnd_eng.cur_word+"  score = "+ttvnd_eng.score + " "+ e_trie.search( ttvnd_eng.cur_word ) );

//            if(i == 0) possibleWordList_eng.add( ttvnd_eng.cur_word);
//            else
            if( e_trie.search( ttvnd_eng.cur_word ) && ttvnd_eng.cur_word.length() > 1 ) {
                possibleWordList_eng.add( ttvnd_eng.cur_word);
////                System.out.println("i "+i+"  word->  "+ttvnd.cur_word+"  score = "+ttvnd.score + " "+ t.search( ttvnd.cur_word ) );
//                System.out.println("Eng -->        above word        ------ >>   found in the db ----<<<  "+ttvnd_eng.cur_word + "  score = "+ttvnd_eng.score);
//                possibleWordList.add(ttvnd.cur_word);
                cnt_found_one_eng++;

                if(cnt_found_one_eng > 5 ) break;
            }
//            else {
//                trietrav_list.remove(i);
//                i--;
//            }
        }
        ///////////////////////// For english

        idxOfBengSugEndStList = possibleWordList.size() - 1;
        for(String nwSt: possibleWordList_eng) {
            possibleWordList.add( nwSt );
        }

//        System.out.println(" idxOfBengSugEndStList   "+ idxOfBengSugEndStList);
        for(String nwSt: possibleWordList) {
//            System.out.println("1481   possibleWordList  "+ nwSt);
        }

        Keyboard currentKeyboard = kv.getKeyboard();

        if(currentKeyboard == mQwertyKeyboard)
            return possibleWordList;
        else
            return possibleWordList_eng;
    }

    public void pickSuggestionManually(int index) {

        isSuggestionTapped = true;
//        System.out.println("MyKeyboard   pickSuggestionMannually starts");
////        System.out.println(" @@ Mykeyboard  pickSuggestionManually  "+index + "  " + allSuggestedWOrdsInEng[ index]+"  " +
//                allSuggestedWOrds[ index].length()+ "  "+suggestionWord+"  " + suggIcWord.length());
        String bnVersion_st =  mComposing_bn_ver.toString();

        mComposing.setLength(0);
        mComposing_bn_ver.setLength(0);
        delCharCorIcLet.clear();

        Keyboard current = kv.getKeyboard();

        if(current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard || current == mSymbolsBngKeyboard){
            setCandidatesViewShown(true);
        }
        else {
//            setCandidatesViewShown(false);
        }
        //int len_stprint = suggestionWord.length();
        int len_stprint = suggIcWord.length();
        char delWord[] = suggestionWord.toCharArray();
//        System.out.println(" isNexWord  " + isNexWord);

        if(current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard || current == mSymbolsBngKeyboard || sugg_flag == true){
            ic.commitText(emojis.get(index),1);
            setCandidatesViewShown(true);
        }
        else{
            if( !isNexWord ) {
//                System.out.println("if( !isNexWord ) " + isNexWord);

//            if( fromType ) len_stprint--;
                for (int i = 0; i < len_stprint; i++) {

                    getCurrentInputConnection().deleteSurroundingText(1, 0);
                }

//                System.out.print("  index" + index + " idxOfEndOfBengSugShow   " + idxOfEndOfBengSugShow );

                if( index >= idxOfEndOfBengSugShow ) {
//                    System.out.println("asche eng sugg e  allSuggestedWOrds[index]  " + allSuggestedWOrds[index] );
//                ic.setComposingText(allSuggestedWOrds[ index ].toCharArray(), 1);
                    String writeEnglishWordFromSugg = allSuggestedWOrds[ index ];
                    for(char ch_now : writeEnglishWordFromSugg.toCharArray() ) {
                        ic.commitText( String.valueOf( ch_now ), 1 );
                    }
                    ic.commitText( String.valueOf( (char) 32 ), 1 );
                    lastWrittenWordWithSwipe = allSuggestedWOrds[ index ];
                    WordShownAfterTappingSuggestion = allSuggestedWOrds[ index ];

                }
                else {
                    printCharinScreen(allSuggestedWOrdsInEng[ index] );
                    ic.commitText( String.valueOf( (char) 32 ), 1 );
                    myData = "SuggestionPicked: ";
                    write_dc_information();
//                if(fromType) next_words_show( bnVersion_st );
//                else
                    WordShownAfterTappingSuggestion = suggIcWord;

                    if(current == mQwertyEngKeyboard){
                        next_words_show_eng( suggIcWord );
                    }
                    else {
                        if(next_word_3gram == true){
                            String combined_2gram = last_word_3gram;
                            combined_2gram = combined_2gram + " " + suggIcWord;
                            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + combined_2gram);
                            next_words_show(combined_2gram);

                            if(possibleNextWOrdsInBng.size() <= 0) {
                                next_words_show(suggIcWord);
                            }
                        }
                        else
                            next_words_show(suggIcWord);

                        last_word_3gram = suggIcWord;
                        next_word_3gram = true;
                    }
                }
            }
            else {

                if(current == mQwertyEngKeyboard){
                    next_words_show_in_screen_eng(possibleNextWOrdsInEng.get(index));
                    nextWordShownAfterTapping = possibleNextWOrdsInEng.get(index);
                    WordShownAfterTappingSuggestion = possibleNextWOrdsInEng.get(index);
//                    System.out.println(" 1764   nextWordShownAfterTapping " + nextWordShownAfterTapping + "  " + nextWordShownAfterTapping.length());

                    next_words_show_eng(possibleNextWOrdsInEng.get(index));
                }
                else {
                    next_words_show_in_screen(possibleNextWOrdsInBng.get(index));
                    nextWordShownAfterTapping = possibleNextWOrdsInBng.get(index);
                    WordShownAfterTappingSuggestion = possibleNextWOrdsInBng.get(index);
//                    System.out.println(" 1764   nextWordShownAfterTapping " + nextWordShownAfterTapping + "  " + nextWordShownAfterTapping.length());

                    if(next_word_3gram == true){
                        String combined_2gram = last_word_3gram;
                        combined_2gram = combined_2gram + " " + possibleNextWOrdsInBng.get(index);
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + combined_2gram);

                        last_word_3gram = possibleNextWOrdsInBng.get(index);
                        next_word_3gram = true;

                        next_words_show(combined_2gram);

                        if(possibleNextWOrdsInBng.size() <= 0) {
                            next_words_show(last_word_3gram);
                        }
                    }
                    else {
                        last_word_3gram = possibleNextWOrdsInBng.get(index);
                        next_word_3gram = true;
                        next_words_show(possibleNextWOrdsInBng.get(index));

                    }
                }

            }

        }



//        System.out.println("MyKeyboard   pickSuggestionMannually ends");

    }

    // banglish to bangla function
    public void printCharinScreen( String stprint) {

//        System.out.println("MyKeyboard   printCharinScreen starts");

//        int[] bengValueOfEngChar = new int[500];
//
//        bengValueOfEngChar[1] = 2438;  // a-> aa
//        bengValueOfEngChar[2] = 2476;  // b -> bo
//        bengValueOfEngChar[3] = 2458;  // c -> cho
//        bengValueOfEngChar[4] = 2470;  // d -> do
//        bengValueOfEngChar[5] = 2447;  // e -> a(ektara)
//        bengValueOfEngChar[6] = 2475;  // f -> fo
//        bengValueOfEngChar[7] = 2455;  // g -> go
//        bengValueOfEngChar[8] = 2489;  // h -> ho
//        bengValueOfEngChar[9] = 2439;  // i -> e(idoor)
//        bengValueOfEngChar[10] = 2460; // j -> borgiyo jo
//        bengValueOfEngChar[11] = 2453; // k -> ko
//        bengValueOfEngChar[12] = 2482; // l -> lo
//        bengValueOfEngChar[13] = 2478; // m -> mo
//        bengValueOfEngChar[14] = 2472; // n -> no
//        bengValueOfEngChar[15] = 2451; // o -> o
//        bengValueOfEngChar[16] = 2474; // p -> po
//        bengValueOfEngChar[17] = 2453; // q -> k
//        bengValueOfEngChar[18] = 2480; // r -> ro
//        bengValueOfEngChar[19] = 2488; // s -> so
//        bengValueOfEngChar[20] = 2468; // t -> to(tumi)
//        bengValueOfEngChar[21] = 2441; // u -> u
//        bengValueOfEngChar[22] = 2477; // v -> vo
//        bengValueOfEngChar[23] = 2451; // w -> o
//        bengValueOfEngChar[24] = 2453; // x -> ko
//        bengValueOfEngChar[25] = 2527; // y -> antosto yo
//        bengValueOfEngChar[26] = 2479; // z -> jo(jemon)
//
//        bengValueOfEngChar[27] = 2494;  // a-> aa-kar
//        bengValueOfEngChar[28] = 2495;  // i -> e-kar(hrossho)
//        bengValueOfEngChar[29] = 2496;  // i -> e-kar(deergho)
//        bengValueOfEngChar[30] = 2497;  // u -> u-kar(hrossho)
//        bengValueOfEngChar[31] = 2498;  // u -> u-kar(deergho)
//        bengValueOfEngChar[32] = 2499;  // r -> ri-kar
//        bengValueOfEngChar[33] = 2503;  // e -> a-kar
//        bengValueOfEngChar[31] = 2504;  // oi-kar (no key)
//        bengValueOfEngChar[32] = 2507;  // o -> o-kar
//        bengValueOfEngChar[33] = 2508;  // ow-kar (no key)
//
//        bengValueOfEngChar[34] = 2509; // hosonto
//        bengValueOfEngChar[36] = 2437;  // swre- o
//        bengValueOfEngChar[37] = 2463;  // TTo
//        bengValueOfEngChar[38] = 2464;  // TTHo
//        bengValueOfEngChar[39] = 2465;  // DDo
//        bengValueOfEngChar[40] = 2465;  // DDo
//        bengValueOfEngChar[41] = 2466;  // DDho
//        bengValueOfEngChar[42] = 2467;  // murdho nno NNA
//
//
//        int len_stprint = stprint.length();
//        int pVal=0;
//        char stprint_charArr[] = stprint.toCharArray();
//        String printIcCommitValue = "";
//        String typedString = "";
//
//        for(int i=0; i<len_stprint; i++) {
//
//           /* ic.commitText( String.valueOf( (char) 2438), 1);*/
////            if(  (stprint_charArr[i] < 'a'  || 'z' <stprint_charArr[i]) && stprint_charArr[ i ] != '`' ) {
////                continue;
////            }
////            else
//            if (i>0 && isHosonto( stprint_charArr[i] ) && !isVowel( stprint_charArr[i-1] )  ) {
//                pVal = 2509;
//            }
//            else if( i>0 && isVowel( stprint_charArr[i] ) && !isVowel( stprint_charArr[i-1] ) && !isHosonto( stprint_charArr[i-1] ) ) {
//                if( stprint_charArr[i] == 'a') pVal = 2494;
//                else if( stprint_charArr[i] == 'e') pVal = 2503;
//                else if( stprint_charArr[i] == 'i') pVal = 2495;
//                else if( stprint_charArr[i] == 'I') pVal = 2496;
//                else if( stprint_charArr[i] == 'o') pVal = 2507;
//                else if( stprint_charArr[i] == 'u') pVal = 2497;
//                else if( stprint_charArr[i] == 'U') pVal = 2498;
//
//            }
//            else if( !isVowel( stprint_charArr[i] ) && i+1<len_stprint && stprint_charArr[ i+1 ] == 'h' ) {
//                if( stprint_charArr[ i ] == 'k' ) pVal = 2454; //kho
//                else if( stprint_charArr[ i ] == 'g' ) pVal = 2456; //gho
//                else if( stprint_charArr[ i ] == 'c' ) pVal = 2459; //cho
//                else if( stprint_charArr[ i ] == 'j' ) pVal = 2461; //jho
//                else if( stprint_charArr[ i ] == 't' ) pVal = 2469; //tho
//                else if( stprint_charArr[ i ] == 'p' ) pVal = 2475; //pho
//                else if( stprint_charArr[ i ] == 'd' ) pVal = 2471; //dho
//                else if( stprint_charArr[ i ] == 'b' ) pVal = 2477; //bho
//                else if( stprint_charArr[ i ] == 's' ) pVal = 2486; // tali-bo-sho
//                else if( stprint_charArr[ i ] == 'T' ) pVal = 2464;  // TTHo
//                else if( stprint_charArr[ i ] == 'D' ) pVal = 2466; //DDHo
//                else if( stprint_charArr[ i ] == 'S' ) pVal = 2487; // murdho no sho
//                else if( stprint_charArr[ i ] == 'R' ) pVal = 2525; // murdho no sho
//                else {
//                    pVal = bengValueOfEngChar[stprint_charArr[i] - 'a' + 1];
//                    i--;
//                }
////                else if( stprint_charArr[ i ] == '' ) pVal = ;
//                i++;
//            }
//            else if( stprint_charArr[i] == 'R' && i+1<len_stprint && stprint_charArr[ i+1 ] == 'I') {
//                pVal = 2499; //ri kar
//                i++;
//            }
//            else if( 'A' <= stprint_charArr[i] && stprint_charArr[i] <= 'Z' ) {
//
//                if( stprint_charArr[ i ] == 'O' ) pVal = 2437;
//                else if( stprint_charArr[ i ] == 'T' ) pVal = 2463;
//                else if( stprint_charArr[ i ] == 'D' ) pVal = 2465;
//                else if( stprint_charArr[ i ] == 'N' ) pVal = 2467;
//                else if( stprint_charArr[ i ] == 'R' ) pVal = 2524;
//
//            }
//            else if(  stprint_charArr[i] =='n'  && i+1<len_stprint && stprint_charArr[ i+1 ] == 'g' ) {
//                pVal = 2434;
//                i++;
//            }
//            else {
//                pVal = bengValueOfEngChar[ stprint_charArr[i]-'a'+1  ] ;
//            }
//
//            /*if( i>0 && !isVowel( stprint_charArr[i] ) &&  !isVowel( stprint_charArr[i-1] )) {
//                ic.commitText( String.valueOf( (char) 2509 ), 1 );
//                typedString = typedString.concat( ""+ ((char) 2509));
//            }*/
////            ic.commitText( String.valueOf( (char) pVal ), 1 );
//            typedString = typedString.concat( ""+ ((char) pVal));
//            //ic.commitText( String.valueOf( (char) bengValueOfEngChar[ stprint_charArr[i]-'a'+1  ]), 1 );
//
//            // printIcCommitValue = printIcCommitValue.concat(""+bengValueOfEngChar[ stprint_charArr[i]-'a'+1  ] );
//            printIcCommitValue = printIcCommitValue.concat(""+pVal );
//            printIcCommitValue = printIcCommitValue.concat(" ");
//        }

//        ic.commitText( String.valueOf( (char) 32 ), 1 );

//        Log.d("printIcCommitValue", printIcCommitValue);
//        Log.d("1845  typedString  --> ", stprint + " " + typedString);
        Keyboard currentKeyboard = kv.getKeyboard();

        if(currentKeyboard == mQwertyKeyboard || currentKeyboard == mProvatKeyboard || currentKeyboard == mProvatKeyboardShift){
            PhoneticParser avro = PhoneticParser.getInstance();
            avro.setLoader(new PhoneticXmlLoader(context));
            String bangla = avro.parse(stprint);

//            System.out.print("func bangla in printcharinscreen>> "+typedString+"avro bangla in print >>"+bangla+">> ");
            String banglaIC = "";

            for(int g = 0;g<bangla.length();g++){
//                System.out.print((char)getIcValueForBengLetters(bangla.charAt(g))+" ");
                ic.commitText( String.valueOf( (char) getIcValueForBengLetters(bangla.charAt(g)) ), 1 );
                banglaIC = banglaIC.concat(""+(char)getIcValueForBengLetters(bangla.charAt(g)));

            }
//            System.out.println(">>>>"+banglaIC);
//            typedString = banglaIC;

            myData = myData.concat( banglaIC+" " );
//        suggIcWord = typedString+" "+banglaIC;
            suggIcWord = banglaIC;
//            System.out.println(">>>>>typedstring >> "+typedString+"  >>>>"+suggIcWord);
            lastWrittenWordWithSwipe = banglaIC+" ";
        }
        else{
            for(int g=0;g<stprint.length();g++){
                ic.commitText(String.valueOf(stprint.charAt(g)),1);
            }

            myData = myData.concat( stprint+" " );
//        suggIcWord = typedString+" "+banglaIC;
            suggIcWord = stprint;
//            System.out.println(">>>>>typedstring >> "+stprint+"  >>>>"+suggIcWord);
            lastWrittenWordWithSwipe = stprint+" ";
        }

    }

    public static boolean isHosonto(char chv) {
        if( chv == '`' ) return true;
        else return  false;
    }

    public static boolean isVowel(char chv ) {
        if( chv == 'a' || chv=='e' || chv == 'i' || chv == 'o' || chv == 'u' ) return true;
        else if( chv == 'I' || chv == 'U' ) return true;
        else return  false;
    }

    public void next_words_show(String cur_word) {

        String text= (String) ic.getExtractedText(new ExtractedTextRequest(), 0).text;
        String sentences[]=text.split("[\n.৷?:;]");
        String lastSentance=sentences[sentences.length-1];

        String words[]=lastSentance.split("[ \n.|,;:]");
        float[][] input=new float[1][vocabulary_for_emoji.size()];
        for(int i=0;i<vocabulary_for_emoji.size();i=i+1)
        {
            input[0][i]=0;
        }

        for(String word:words)
        {
            if(vocabulary_for_emoji.containsKey(word))
            {
                input[0][vocabulary_for_emoji.get(word)]= (float) (input[0][vocabulary_for_emoji.get(word)]+1.0);
            }
        }

        float[][] prediction=new float[1][emojilist.size()];
         prediction = emojiPredictorLSTM.doInference(input, prediction);

        List<Pair<Float, Integer> > emoji_probability=new ArrayList<Pair<Float, Integer>>();
        for(int i=0;i<emojilist.size();i++)
        {
            emoji_probability.add(new Pair<Float, Integer> (new Float(prediction[0][i]), new Integer(i)));
        }
        Collections.sort(emoji_probability, new Comparator<Pair<Float, Integer>>() {
            @Override
            public int compare(Pair<Float, Integer> floatIntegerPair, Pair<Float, Integer> t1) {
                return -Float.compare(floatIntegerPair.first, t1.first);
            }
        });


//        System.out.println(" cur word   "+ cur_word+ "  mp1_word_id.get( cur_word )   " +mp1_word_id.get( cur_word ));
        possibleNextWOrdsInBng.clear();
        for(int i=0;i<3;i++)
        {
//            System.out.println("tt 4352 "+emojilist.get(emoji_probability.get(i).second));
            possibleNextWOrdsInBng.add(emojilist.get(emoji_probability.get(i).second));
//            possibleNextWOrdsInBng.add("ok");
        }

        boolean trigram_flag = false;
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> trigram "+cur_word +" "  + mp1_word_id_trigram.get(cur_word) + " " + mp1_word_id.get( cur_word ));
        if(mp1_word_id.get( cur_word ) == null ) {
            if(mp1_word_id_trigram.get(cur_word) != null){
                trigram_flag = true;
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> trigram");
            }
            else {
                sugg_flag = true;
                for(int i=3;i<10;i++)
                {
//                    System.out.println("tt 4376 "+emojilist.get(emoji_probability.get(i).second));
                    possibleNextWOrdsInBng.add(emojilist.get(emoji_probability.get(i).second));
                }
                mCandidateView.setSuggestions(possibleNextWOrdsInBng, true, true);
                setCandidatesViewShown(true);
                return;
            }
        }
        List< HashMap< String, Integer > > list_of_words_mp_in_function;
        int word_idx = 0;
        if(trigram_flag){
            word_idx = mp1_word_id_trigram.get( cur_word );
            list_of_words_mp_in_function = list_of_words_mp_trigram;
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> trigram " + word_idx);
        }
        else {
            word_idx = mp1_word_id.get(cur_word);
            list_of_words_mp_in_function = list_of_words_mp;
        }

        HashMap<String, Integer> tt_cur_mp = list_of_words_mp_in_function.get(word_idx);

        int mx = 0;

//        for (Map.Entry<String, Integer> word_mp : tt_cur_mp.entrySet()) {
////            System.out.println(word_mp.getKey() + " "+word_mp.getValue() +" $" );
//
//            if( word_mp.getValue() < mx ) continue;
//            if( word_mp.getValue() > mx ) {
//                possibleNextWOrdsInBng.clear();
//            }
//            possibleNextWOrdsInBng.add( word_mp.getKey() );
//            mx = word_mp.getValue();
////                cnt_2++;   //                if(cnt_2>15) break;
//        }

        Set<Entry<String, Integer>> set = tt_cur_mp.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
        Collections.sort( list, new Comparator<Entry<String, Integer>>()
        {
            public int compare( Entry<String, Integer> o1, Entry<String, Integer> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );

        int cnt_next_word = 0;
        for(Entry<String, Integer> entry:list){
//            System.out.println(entry.getKey()+" ==== "+entry.getValue());
            possibleNextWOrdsInBng.add(  entry.getKey() );
//            if( cnt_next_word++ > 5 ) break;
        }

        for(String ss:possibleNextWOrdsInBng ) {
//            System.out.println(" Next Word ----->  "+ ss);
//            next_words_show_in_screen(ss);
        }
        isNexWord = true;
        setCandidatesViewShown(true);
        mCandidateView.setSuggestions(possibleNextWOrdsInBng, true, true);
    }

    public void next_words_show_eng(String cur_word) {
        String cur_word_up = "";
        for(int i=0;i<cur_word.length();i++){
            if(i==0){
                cur_word_up = cur_word_up + Character.toUpperCase(cur_word.charAt(i));
            }
            else
                cur_word_up = cur_word_up + cur_word.charAt(i);
        }
//        System.out.println(" cur word upcase eng  "+ cur_word_up +" cur word eng  "+ cur_word+ "  mp1_word_id.get( cur_word )   " +mp1_word_id_eng.get( cur_word ));
        possibleNextWOrdsInEng.clear();

        if(mp1_word_id_eng.get( cur_word_up ) == null && mp1_word_id_eng.get( cur_word ) == null ) {
            System.out.println("naii");
            sugg_flag = true;
            mCandidateView.setSuggestions(emojis, true, true);
            setCandidatesViewShown(true);
            return;
        }

        if(mp1_word_id_eng.get( cur_word ) != null) {
            System.out.println("naiii");
            int word_idx = mp1_word_id_eng.get(cur_word);
            HashMap<String, Integer> tt_cur_mp = list_of_words_mp_eng.get(word_idx);

            int mx = 0;

//        for (Map.Entry<String, Integer> word_mp : tt_cur_mp.entrySet()) {
////            System.out.println(word_mp.getKey() + " "+word_mp.getValue() +" $" );
//
//            if( word_mp.getValue() < mx ) continue;
//            if( word_mp.getValue() > mx ) {
//                possibleNextWOrdsInBng.clear();
//            }
//            possibleNextWOrdsInBng.add( word_mp.getKey() );
//            mx = word_mp.getValue();
////                cnt_2++;   //                if(cnt_2>15) break;
//        }

            Set<Entry<String, Integer>> set = tt_cur_mp.entrySet();
            List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
            Collections.sort(list, new Comparator<Entry<String, Integer>>() {
                public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            });


            for (Entry<String, Integer> entry : list) {
//                System.out.println(entry.getKey() + " ==== " +entry.getKey().length() + "length" + entry.getValue());
                boolean flag = false;
                for(int j = 0;j<entry.getKey().length();j++) {
                    if ((entry.getKey().charAt(j) >= 'a' && entry.getKey().charAt(j) >= 'z') || (entry.getKey().charAt(j) >= 'A' && entry.getKey().charAt(j) >= 'Z')) {
                        flag = true;
                        break;
                    }
                }
                if(flag)
                    possibleNextWOrdsInEng.add(entry.getKey());
//            if( cnt_next_word++ > 5 ) break;
            }
        }

        if(mp1_word_id_eng.get( cur_word_up ) != null) {
            System.out.println("naiiii");
            int word_idx_up = mp1_word_id_eng.get(cur_word_up);
            HashMap<String, Integer> tt_cur_mp_up = list_of_words_mp_eng.get(word_idx_up);

            Set<Entry<String, Integer>> set_up = tt_cur_mp_up.entrySet();
            List<Entry<String, Integer>> list_up = new ArrayList<Entry<String, Integer>>(set_up);
            Collections.sort(list_up, new Comparator<Entry<String, Integer>>() {
                public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            });

            int cnt_next_word = 0;
            for (Entry<String, Integer> entry : list_up) {
//                System.out.println(entry.getKey() + " ==== " +entry.getKey().length() + "length" + entry.getValue());
                boolean flag = false;
                for(int j = 0;j<entry.getKey().length();j++) {
                    if ((entry.getKey().charAt(j) >= 'a' && entry.getKey().charAt(j) >= 'z') || (entry.getKey().charAt(j) >= 'A' && entry.getKey().charAt(j) >= 'Z')) {
                        flag = true;
                        break;
                    }
                }
                if(flag)
                    possibleNextWOrdsInEng.add(entry.getKey());
//            if( cnt_next_word++ > 5 ) break;
            }
        }

        for(String ss:possibleNextWOrdsInEng ) {
//            System.out.println(" Next Word eng ----->  "+ ss);
//            next_words_show_in_screen(ss);
        }
        isNexWord = true;
        setCandidatesViewShown(true);
        mCandidateView.setSuggestions(possibleNextWOrdsInEng, true, true);
    }

    public void next_words_show_in_screen(String rcvedBengString) {
        System.out.println("tt 4582 emoji got "+ rcvedBengString);
        ic.commitText(rcvedBengString, 1);

//        if(rcvedBengString.length()==1)
//        {
//            System.out.println("tt 4582 emoji got "+ rcvedBengString);
//            ic.commitText(rcvedBengString, 1);
//            return;
//        }

//        String nextWordcur ="";
//        int pVal=0;
//        for(int i=0; i<rcvedBengString.length(); i++ ) {
//            pVal = getIcValueForBengLetters( rcvedBengString.charAt(i) );
////            if(pVal == 0) System.out.println("Character not Found   " + rcvedBengString.charAt(i));
//            ic.commitText( String.valueOf( (char) pVal ), 1 );
//            nextWordcur = nextWordcur.concat( ""+ ((char) pVal));
//        }
        ic.commitText( String.valueOf( (char) 32 ), 1 );

//        System.out.println("Next word To be printed in screen = " + nextWordcur);
    }

    public void next_words_show_in_screen_eng(String rcvedengString) {

        String nextWordcur ="";
        for(int i=0; i<rcvedengString.length(); i++ ) {
            ic.commitText( String.valueOf( rcvedengString.charAt(i)), 1 );
            nextWordcur = nextWordcur.concat( ""+ rcvedengString.charAt(i));
        }
        ic.commitText( String.valueOf( (char) 32 ), 1 );

//        System.out.println("Next word To be printed in screen english = " + nextWordcur);
    }


    // next word prediction intialization
    public void next_word_init() {
        read_comments();

//        System.out.println("mp1_word_id  " + mp1_word_id.size() );
//        int cnt = 0;
//
//        int sz = mp1_word_id.size();
//        for (Map.Entry<String, Integer> entry : mp1_word_id.entrySet())
//        {
////            System.out.println(entry.getKey() + "    "+entry.getValue() +"                       ----> --->        " );
//            int idx = entry.getValue();
//            HashMap< String, Integer > tt_cur_mp = list_of_words_mp.get(idx);
////            int cnt_2  = 0;
//            for (Map.Entry<String, Integer> word_mp : tt_cur_mp.entrySet()) {
////                System.out.println(word_mp.getKey() + " "+word_mp.getValue() +" $" );
////                cnt_2++;   //                if(cnt_2>15) break;
//            }
////            cnt++;
//        }

    }

    // for next word prediction file read
    private void read_comments() {
        try {
            StringBuffer sBuffer = new StringBuffer();

            InputStream is = this.getResources().openRawResource(R.raw.test_corpus);

            InputStream is_eng = this.getResources().openRawResource(R.raw.test_corpus_eng);

//            BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(is));

            // Open the file that is the first
            // command line parameter
//            FileInputStream fstream = new FileInputStream(documentsPath);
//            // Get the object of DataInputStream
//            DataInputStream in = new DataInputStream(fstream);
//            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
//            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            BufferedReader br =  new BufferedReader(new InputStreamReader(is, "UTF-8"));

            BufferedReader br_eng =  new BufferedReader(new InputStreamReader(is_eng));
            String strLine,strLine_eng;

            int fileCounter=1;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                // Print the content on the console
                strLine = strLine + "\n";
////            System.out.println (strLine);
                al_comments.add(strLine);

                String tokens[] = strLine.split("[\u002c\u0964\u003f\u0021\\u0022\u0020\u002D\u003A"
                        + "\u09e6\u09e7\u09e8\u09e9\u09ea\u09eb\u09ec\u09ed\u09ee\u09ef]+");

                int len_tok = tokens.length;
//            for ( String ss : tokens) {
                for(int tk_idx = 0; tk_idx<len_tok; tk_idx++ ) {
                    String ss = tokens[ tk_idx ];

                    if(tk_idx+1<len_tok) {

                        if( mp1_word_id.get( ss ) == null ) {
                            mp1_word_id.put( ss,  mp1_word_id.size());
                        }
                        int id =   mp1_word_id.get(ss);

                        if(list_of_words_mp.size() <= id ) {
                            list_of_words_mp.add( new HashMap< String, Integer >() );
                        }
                        HashMap< String, Integer > tt_cur_mp = list_of_words_mp.get(id);

                        String ss_next = tokens[ tk_idx+1 ];

                        if(tt_cur_mp.get( ss_next ) == null ) {
                            tt_cur_mp.put( ss_next, 1 );
                        }
                        else {
                            int cur_freq = tt_cur_mp.get( ss_next);
                            tt_cur_mp.put(ss_next, cur_freq+1);
                        }
                    }

                    //FOR TRIGRAM
                    if(tk_idx+2<len_tok) {

                        String ss_next1 = tokens[ tk_idx  + 1];
                        String ss_2gram = ss + " " +ss_next1;

                        if( mp1_word_id_trigram.get( ss_2gram ) == null ) {
                            mp1_word_id_trigram.put( ss_2gram,  mp1_word_id_trigram.size());
                        }
                        int id =   mp1_word_id_trigram.get(ss_2gram);

                        if(list_of_words_mp_trigram.size() <= id ) {
                            list_of_words_mp_trigram.add( new HashMap< String, Integer >() );
                        }
                        HashMap< String, Integer > tt_cur_mp_trigram = list_of_words_mp_trigram.get(id);

                        String ss_next2 = tokens[ tk_idx + 2 ];

                        if(tt_cur_mp_trigram.get( ss_next2 ) == null ) {
                            tt_cur_mp_trigram.put( ss_next2, 1 );
                        }
                        else {
                            int cur_freq = tt_cur_mp_trigram.get( ss_next2);
                            tt_cur_mp_trigram.put(ss_next2, cur_freq+1);
                        }
                    }
//                    //                System.out.print(ss+" + ");
                }
            }
            //Close the input stream
//            in.close();
            is.close();

            //next word for english
            while ((strLine_eng = br_eng.readLine()) != null)   {
                // Print the content on the console
                strLine_eng = strLine_eng + "\n";
////            System.out.println (strLine);
                al_comments_eng.add(strLine_eng);

                String tokens_eng[] = strLine_eng.split("[\u002c\u0964\u003f\u0021\\u0022\u0020\u002D\u003A"
                        + "\u09e6\u09e7\u09e8\u09e9\u09ea\u09eb\u09ec\u09ed\u09ee\u09ef]+");

                int len_tok = tokens_eng.length;
//            for ( String ss : tokens) {
                for(int tk_idx = 0; tk_idx<len_tok; tk_idx++ ) {
                    String ss = tokens_eng[ tk_idx ];

                    if(tk_idx+1<len_tok) {

                        if( mp1_word_id_eng.get( ss ) == null ) {
                            mp1_word_id_eng.put( ss,  mp1_word_id_eng.size());
                        }
                        int id =   mp1_word_id_eng.get(ss);

                        if(list_of_words_mp_eng.size() <= id ) {
                            list_of_words_mp_eng.add( new HashMap< String, Integer >() );
                        }
                        HashMap< String, Integer > tt_cur_mp = list_of_words_mp_eng.get(id);

                        String ss_next = tokens_eng[ tk_idx+1 ];

                        if(tt_cur_mp.get( ss_next ) == null ) {
                            tt_cur_mp.put( ss_next, 1 );
                        }
                        else {
                            int cur_freq = tt_cur_mp.get( ss_next);
                            tt_cur_mp.put(ss_next, cur_freq+1);
                        }
                    }
//                    //                System.out.print(ss+" + ");
                }
            }
            //Close the input stream
//            in.close();
            is_eng.close();

        } catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }

    public int getIcValueForBengLetters(char ch_now) {
        if(ch_now == 'অ' ) {
            return 2437;
        }
        else if(ch_now == 'আ' ) {
            return 2438;
        }
        else if( ch_now == 'ই' ) {
            return 2439;
        }
        else if(ch_now == 'ৎ' ) {
            return 2510;
        }
        else if( ch_now == 'ঈ' ) {
            return 2440;
        }
        else if( ch_now == 'উ' ) {
            return 2441;
        }
        else if( ch_now == 'ঊ' ) {
            return 2442;
        }
        else if( ch_now == 'ঋ' ) {
            return 2443;
        }
        else if( ch_now == 'ৠ' ) {
            return 2528;
        }
        else if( ch_now == 'ঌ' ) {
            return 2444;
        }
        else if( ch_now == 'ৡ' ) {
            return 2529;
        }
        else if( ch_now == 'এ' ) {
            return 2447;
        }
        else if( ch_now == 'ঐ' ) {
            return 2448;
        }
        else if( ch_now == 'ও' ) {
            return 2451;
        }
        else if( ch_now == 'ঔ' ) {
            return 2452;
        }
        else if( ch_now == 'ক' ) {
            return 2453;
        }
        else if( ch_now == 'খ' ) {
            return 2454;
        }
        else if( ch_now == 'গ' ) {
            return 2455;
        }
        else if( ch_now == 'ঘ' ) {
            return 2456;
        }
        else if( ch_now == 'ঙ' ) {
            return 2457;
        }
        else if( ch_now == 'চ' ) {
            return 2458;
        }
        else if( ch_now == 'ছ' ) {
            return 2459;
        }
        else if( ch_now == 'জ' ) {
            return 2460;
        }
        else if( ch_now == 'ঝ' ) {
            return 2461;
        }
        else if( ch_now == 'ঞ' ) {
            return 2462;
        }
        else if( ch_now == 'ট' ) {
            return 2463;
        }
        else if( ch_now == 'ঠ' ) {
            return 2464;
        }
        else if( ch_now == 'ড' ) {
            return 2465;
        }
        else if( ch_now == 'ঢ' ) {
            return 2466;
        }
        else if( ch_now == 'ণ' ) {
            return 2467;
        }
        else if( ch_now == 'ত' ) {
            return 2468;
        }
        else if( ch_now == 'থ' ) {
            return 2469;
        }
        else if( ch_now == 'দ' ) {
            return 2470;
        }
        else if( ch_now == 'ধ' ) {
            return 2471;
        }
        else if( ch_now == 'ন' ) {
            return 2472;
        }
        else if( ch_now == 'প' ) {
            return 2474;
        }
        else if( ch_now == 'ফ' ) {
            return 2475;
        }
        else if( ch_now == 'ব' ) {
            return 2476;
        }
        else if( ch_now == 'ভ' ) {
            return 2477;
        }
        else if( ch_now == 'ম' ) {
            return 2478;
        }
        else if( ch_now == 'য' ) {
            return 2479;
        }
        else if( ch_now == 'র' ) {
            return 2480;
        }
        else if( ch_now == 'ল' ) {
            return 2482;
        }
        else if( ch_now == 'শ' ) {
            return 2486;
        }
        else if( ch_now == 'ষ' ) {
            return 2487;
        }
        else if( ch_now == 'স' ) {
            return 2488;
        }
        else if( ch_now == 'হ' ) {
            return 2489;
        }
        else if( ch_now == 'ড়' ) {
            return 2524;
        }
        else if( ch_now == 'ঢ়' ) {
            return 2525;
        }
        else if( ch_now == 'য়' ) {
            return 2527;
        }
        else if( ch_now == 'ৰ' ) {
            return 2544;
        }
        else if( ch_now == 'ৱ' ) {
            return 2545;
        }
        else if( ch_now == 'ঁ' ) {
            return 2433;
        }
        else if( ch_now == 'ং' ) {
            return 2434;
        }
        else if( ch_now == 'ঃ' ) {
            return 2435;
        }
        else if( ch_now == '়' ) {
            return 2492;
        }
        else if( ch_now == 'া' ) {
            return 2494;
        }
        else if( ch_now == 'ি' ) {
            return 2495;
        }
        else if( ch_now == 'ী' ) {
            return 2496;
        }
        else if( ch_now == 'ু' ) {
            return 2497;
        }
        else if( ch_now == 'ূ' ) {
            return 2498;
        }
        else if( ch_now == 'ৃ' ) {
            return 2499;
        }
        else if( ch_now == 'ৄ' ) {
            return 2500;
        }
        else if( ch_now == 'ে' ) {
            return 2503;
        }
        else if( ch_now == 'ৈ' ) {
            return 2504;
        }
        else if( ch_now == 'ো' ) {
            return 2507;
        }
        else if( ch_now == 'ৌ' ) {
            return 2508;
        }
        else if( ch_now == '্' ) {
            return 2509;
        }
        else if( ch_now == 'ৗ' ) {
            return 2519;
        }
        else if( ch_now == '০' ) {
            return 2534;
        }
        else if( ch_now == '১' ) {
            return 2535;
        }
        else if( ch_now == '২' ) {
            return 2536;
        }
        else if( ch_now == '৩' ) {
            return 2537;
        }
        else if( ch_now == '৪' ) {
            return 2538;
        }
        else if( ch_now == '৫' ) {
            return 2539;
        }
        else if( ch_now == '৬' ) {
            return 2540;
        }
        else if( ch_now == '৭' ) {
            return 2541;
        }
        else if( ch_now == '৮' ) {
            return 2542;
        }
        else if( ch_now == '৯' ) {
            return 2543;
        }
        return 0;

//        else if( ch_now == '' ) {
//                    return ;
//                }
    }

    public char getEngVersionBengLet(char ch_now) {

//        if( ) {
//            return 'q';
//        }
//        else if( ) {
//            return 'w';
//        }
//        else
        if( ch_now == 'এ' || ch_now == 'ে' ) {
            return 'e';
        }
        else if( ch_now == 'র' || ch_now == 'ড়' || ch_now == 'ঢ়' ) {
            return 'r';
        }
        else if( ch_now == 'ত' || ch_now == 'থ' || ch_now == 'ট' || ch_now == 'ঠ') {
            return 't';
        }
        else if( ch_now == 'য়') {
            return 'y';
        }
        else if( ch_now == 'উ' || ch_now == 'ঊ' || ch_now == 'ু' || ch_now == 'ূ' ) {
            return 'u';
        }
        else if( ch_now == 'ই' || ch_now == 'ঈ' ||  ch_now == 'ি' || ch_now == 'ী' ) {
            return 'i';
        }
        else if(ch_now == 'অ' || ch_now == 'ও' || ch_now == 'ঔ' || ch_now == 'ো' || ch_now == 'ৌ' ) {
            return 'o';
        }
        else if( ch_now == 'প'  ) { //first row ending
            return 'p';
        }
        else if( ch_now == 'আ' || ch_now == 'া' ) { //second row starting
            return 'a';
        }
        else if( ch_now == 'শ' ||  ch_now == 'ষ' || ch_now == 'স' ) {
            return 's';
        }
        else if( ch_now == 'দ' || ch_now == 'ধ' ||ch_now == 'ড' || ch_now == 'ঢ'  ) {
            return 'd';
        }
        else if( ch_now == 'ফ' ) {
            return 'f';
        }
        else if( ch_now == 'গ' || ch_now == 'ঘ' ) {
            return 'g';
        }
        else if( ch_now == 'হ' ) {
            return 'h';
        }
        else if( ch_now == 'জ' || ch_now == 'ঝ' ) {
            return 'j';
        }
        else if( ch_now == 'ক' || ch_now == 'খ' ) {
            return 'k';
        }
        else if( ch_now == 'ল' ) {
            return 'l';
        }
        else if( ch_now == '্' ) {
            return '`';
        }
        else if( ch_now == 'য' ) {
            return 'z';
        }
//        else if(  ) {
//            return 'x';
//        }
        else if( ch_now == 'চ' || ch_now == 'ছ' ) {
            return 'c';
        }
        else if( ch_now == 'ভ' ) {
            return 'v';
        }
        else if( ch_now == 'ব' ) {
            return 'b';
        }
        else if( ch_now == 'ন' || ch_now == 'ণ') {
            return 'n';
        }
        else if( ch_now == 'ম' ) {
            return 'm';
        }
//        else if( ch_now == '' ) {
//                    return ;
//                }
        return '`';
    }

    public static void printCharInConsoleRun(Context context, String stprint) {
        int[] bengValueOfEngChar = new int[500];

        bengValueOfEngChar[1] = 2438;  // a-> aa
        bengValueOfEngChar[2] = 2476;  // b -> bo
        bengValueOfEngChar[3] = 2458;  // c -> cho
        bengValueOfEngChar[4] = 2470;  // d -> do
        bengValueOfEngChar[5] = 2447;  // e -> a(ektara)
        bengValueOfEngChar[6] = 2475;  // f -> fo
        bengValueOfEngChar[7] = 2455;  // g -> go
        bengValueOfEngChar[8] = 2489;  // h -> ho
        bengValueOfEngChar[9] = 2439;  // i -> e(idoor)
        bengValueOfEngChar[10] = 2460; // j -> borgiyo jo
        bengValueOfEngChar[11] = 2453; // k -> ko
        bengValueOfEngChar[12] = 2482; // l -> lo
        bengValueOfEngChar[13] = 2478; // m -> mo
        bengValueOfEngChar[14] = 2472; // n -> no
        bengValueOfEngChar[15] = 2451; // o -> o
        bengValueOfEngChar[16] = 2474; // p -> po
        bengValueOfEngChar[17] = 2453; // q -> k
        bengValueOfEngChar[18] = 2480; // r -> ro
        bengValueOfEngChar[19] = 2488; // s -> so
        bengValueOfEngChar[20] = 2468; // t -> to(tumi)
        bengValueOfEngChar[21] = 2441; // u -> u
        bengValueOfEngChar[22] = 2477; // v -> vo
        bengValueOfEngChar[23] = 2451; // w -> o
        bengValueOfEngChar[24] = 2453; // x -> ko
        bengValueOfEngChar[25] = 2527; // y -> antosto yo
        bengValueOfEngChar[26] = 2479; // z -> jo(jemon)

        bengValueOfEngChar[27] = 2494;  // a-> aa-kar
        bengValueOfEngChar[28] = 2495;  // i -> e-kar(hrossho)
        bengValueOfEngChar[29] = 2496;  // i -> e-kar(deergho)
        bengValueOfEngChar[30] = 2497;  // u -> u-kar(hrossho)
        bengValueOfEngChar[31] = 2498;  // u -> u-kar(deergho)
        bengValueOfEngChar[32] = 2499;  // r -> ri-kar
        bengValueOfEngChar[33] = 2503;  // e -> a-kar
        bengValueOfEngChar[31] = 2504;  // oi-kar (no key)
        bengValueOfEngChar[32] = 2507;  // o -> o-kar
        bengValueOfEngChar[33] = 2508;  // ow-kar (no key)

        bengValueOfEngChar[34] = 2509;  // hosonto
        bengValueOfEngChar[36] = 2437;  // swre- o
        bengValueOfEngChar[37] = 2463;  // TTo
        bengValueOfEngChar[38] = 2464;  // TTHo
        bengValueOfEngChar[39] = 2465;  // DDo
        bengValueOfEngChar[40] = 2465;  // DDo
        bengValueOfEngChar[41] = 2466;  // DDho
        bengValueOfEngChar[42] = 2467;  // murdho nno NNA




        int len_stprint = stprint.length();

        int[] wordCharValarr= new int[50];

        int pVal=0;
        char stprint_charArr[] = stprint.toCharArray();
        String printIcCommitValue = "";
        String suggestString="";

//        for(int i=0; i<len_stprint; i++) {
//
//           /* ic.commitText( String.valueOf( (char) 2438), 1);*/
////            if(  (stprint_charArr[i] < 'a'  || 'z' <stprint_charArr[i]) && stprint_charArr[ i ] != '`') {
////                continue;
////            }
////            else
//            if (i>0 && isHosonto( stprint_charArr[i] ) && !isVowel( stprint_charArr[i-1] )  ) {
//                pVal = 2509;
//            }
//            else if( i>0 && isVowel( stprint_charArr[i] ) && !isVowel( stprint_charArr[i-1] ) && !isHosonto( stprint_charArr[i-1] ) ) {
//                if( stprint_charArr[i] == 'a') pVal = 2494;
//                else if( stprint_charArr[i] == 'e') pVal = 2503;
//                else if( stprint_charArr[i] == 'i') pVal = 2495;
//                else if( stprint_charArr[i] == 'I') pVal = 2496;
//                else if( stprint_charArr[i] == 'o') pVal = 2507;
//                else if( stprint_charArr[i] == 'u') pVal = 2497;
//                else if( stprint_charArr[i] == 'U') pVal = 2498;
//
//            }
//            else if( !isVowel( stprint_charArr[i] ) && i+1<len_stprint && stprint_charArr[ i+1 ] == 'h' ) {
//                if( stprint_charArr[ i ] == 'k' ) pVal = 2454; //kho
//                else if( stprint_charArr[ i ] == 'g' ) pVal = 2456; //gho
//                else if( stprint_charArr[ i ] == 'c' ) pVal = 2459; //cho
//                else if( stprint_charArr[ i ] == 'j' ) pVal = 2461; //jho
//                else if( stprint_charArr[ i ] == 't' ) pVal = 2469; //tho
//                else if( stprint_charArr[ i ] == 'p' ) pVal = 2475; //pho
//                else if( stprint_charArr[ i ] == 'd' ) pVal = 2471; //dho
//                else if( stprint_charArr[ i ] == 'b' ) pVal = 2477; //bho
//                else if( stprint_charArr[ i ] == 's' ) pVal = 2486; // tali-bo-sho
//                else if( stprint_charArr[ i ] == 'T' ) pVal = 2464;  // TTHo
//                else if( stprint_charArr[ i ] == 'D' ) pVal = 2466; //DDHo
//                else if( stprint_charArr[ i ] == 'S' ) pVal = 2487; // murdho no sho
//                else if( stprint_charArr[ i ] == 'R' ) pVal = 2525; // murdho no sho
//                else {
//                    pVal = bengValueOfEngChar[stprint_charArr[i] - 'a' + 1];
//                    i--;
//                }
////                else if( stprint_charArr[ i ] == '' ) pVal = ;
//               /* else if( stprint_charArr[ i ] == '' ) pVal = ;
//                else if( stprint_charArr[ i ] == '' ) pVal = ;*/
//                i++;
//            }
//            else if( stprint_charArr[i] == 'R' && i+1<len_stprint && stprint_charArr[ i+1 ] == 'I') {
//                pVal = 2499; //ri kar
//                i++;
//            }
//            else if( 'A' <= stprint_charArr[i] && stprint_charArr[i] <= 'Z' ) {
//
//                if( stprint_charArr[ i ] == 'O' ) pVal = 2437;
//                else if( stprint_charArr[ i ] == 'T' ) pVal = 2463;
//                else if( stprint_charArr[ i ] == 'D' ) pVal = 2465;
//                else if( stprint_charArr[ i ] == 'N' ) pVal = 2467;
//                else if( stprint_charArr[ i ] == 'R' ) pVal = 2524;
//            }
//            else if(  stprint_charArr[i] =='n'  && i+1<len_stprint && stprint_charArr[ i+1 ] == 'g' ) {
//                pVal = 2434;
//                i++;
//            }
//            else {
//                pVal = bengValueOfEngChar[ stprint_charArr[i]-'a'+1  ] ;
//            }
//
//
//////            System.out.print( ((char) pVal));
//////            System.out.println(pVal+ " "+ ((char) pVal) );
//            wordCharValarr[i] = pVal;
//            suggestString = suggestString.concat( ""+ ((char) pVal) );
////            Log.d("key1", ""+ ((char) pVal) );
//            //ic.commitText( String.valueOf( (char) pVal ), 1 );
//            //ic.commitText( String.valueOf( (char) bengValueOfEngChar[ stprint_charArr[i]-'a'+1  ]), 1 );
//
//            // printIcCommitValue = printIcCommitValue.concat(""+bengValueOfEngChar[ stprint_charArr[i]-'a'+1  ] );
//            printIcCommitValue = printIcCommitValue.concat(""+pVal );
//            printIcCommitValue = printIcCommitValue.concat(" ");
//        }
//
////        ic.commitText( String.valueOf( (char) 32 ), 1 );
////        System.out.print(pVal+ " "+ ((char) 32) );
//        wordCharValarr[len_stprint] = 32;

//        Log.d("sttprint", )

//        Log.d("printCharInConsoleRun", printIcCommitValue);
        Log.d("suggestString", stprint+"  "+suggestString);

        PhoneticParser avro = PhoneticParser.getInstance();
        avro.setLoader(new PhoneticXmlLoader(context));
        String bangla = avro.parse(stprint);

//        System.out.print("func bangla in printcharinconsole>> "+suggestString+"avro bangla in print >>"+bangla+">> ");
        String banglaIC = "";
        for(int g = 0;g<bangla.length();g++){
            int val = 0;
            if(bangla.charAt(g)== 'অ' ) {
                val= 2437;
            }
            else if(bangla.charAt(g) == 'ৎ' ) {
                val= 2510;
            }
            else if(bangla.charAt(g) == 'আ' ) {
                val= 2438;
            }
            else if( bangla.charAt(g) == 'ই' ) {
                val= 2439;
            }
            else if(bangla.charAt(g)== 'ঈ' ) {
                val= 2440;
            }
            else if( bangla.charAt(g) == 'উ' ) {
                val= 2441;
            }
            else if(bangla.charAt(g) == 'ঊ' ) {
                val= 2442;
            }
            else if( bangla.charAt(g) == 'ঋ' ) {
                val= 2443;
            }
            else if( bangla.charAt(g) == 'ৠ' ) {
                val= 2528;
            }
            else if( bangla.charAt(g) == 'ঌ' ) {
                val= 2444;
            }
            else if( bangla.charAt(g) == 'ৡ' ) {
                val= 2529;
            }
            else if( bangla.charAt(g) == 'এ' ) {
                val= 2447;
            }
            else if( bangla.charAt(g) == 'ঐ' ) {
                val= 2448;
            }
            else if( bangla.charAt(g) == 'ও' ) {
                val= 2451;
            }
            else if(bangla.charAt(g) == 'ঔ' ) {
                val= 2452;
            }
            else if( bangla.charAt(g) == 'ক' ) {
                val= 2453;
            }
            else if( bangla.charAt(g) == 'খ' ) {
                val= 2454;
            }
            else if( bangla.charAt(g) == 'গ' ) {
                val= 2455;
            }
            else if( bangla.charAt(g) == 'ঘ' ) {
                val=2456;
            }
            else if( bangla.charAt(g) == 'ঙ' ) {
                val= 2457;
            }
            else if( bangla.charAt(g) == 'চ' ) {
                val=2458;
            }
            else if( bangla.charAt(g) == 'ছ' ) {
                val= 2459;
            }
            else if( bangla.charAt(g) == 'জ' ) {
                val= 2460;
            }
            else if( bangla.charAt(g) == 'ঝ' ) {
                val= 2461;
            }
            else if( bangla.charAt(g) == 'ঞ' ) {
                val= 2462;
            }
            else if( bangla.charAt(g) == 'ট' ) {
                val= 2463;
            }
            else if(bangla.charAt(g) == 'ঠ' ) {
                val= 2464;
            }
            else if(bangla.charAt(g) == 'ড' ) {
                val= 2465;
            }
            else if( bangla.charAt(g) == 'ঢ' ) {
                val= 2466;
            }
            else if( bangla.charAt(g) == 'ণ' ) {
                val= 2467;
            }
            else if( bangla.charAt(g) == 'ত' ) {
                val= 2468;
            }
            else if( bangla.charAt(g) == 'থ' ) {
                val= 2469;
            }
            else if(bangla.charAt(g) == 'দ' ) {
                val= 2470;
            }
            else if(bangla.charAt(g) == 'ধ' ) {
                val= 2471;
            }
            else if( bangla.charAt(g) == 'ন' ) {
                val= 2472;
            }
            else if( bangla.charAt(g) == 'প' ) {
                val= 2474;
            }
            else if( bangla.charAt(g) == 'ফ' ) {
                val= 2475;
            }
            else if( bangla.charAt(g) == 'ব' ) {
                val= 2476;
            }
            else if( bangla.charAt(g) == 'ভ' ) {
                val=2477;
            }
            else if( bangla.charAt(g) == 'ম' ) {
                val= 2478;
            }
            else if( bangla.charAt(g) == 'য' ) {
                val= 2479;
            }
            else if( bangla.charAt(g) == 'র' ) {
                val= 2480;
            }
            else if( bangla.charAt(g) == 'ল' ) {
                val= 2482;
            }
            else if( bangla.charAt(g) == 'শ' ) {
                val= 2486;
            }
            else if( bangla.charAt(g) == 'ষ' ) {
                val= 2487;
            }
            else if( bangla.charAt(g) == 'স' ) {
                val= 2488;
            }
            else if( bangla.charAt(g) == 'হ' ) {
                val= 2489;
            }
            else if( bangla.charAt(g) == 'ড়' ) {
                val= 2524;
            }
            else if(bangla.charAt(g) == 'ঢ়' ) {
                val= 2525;
            }
            else if(bangla.charAt(g) == 'য়' ) {
                val= 2527;
            }
            else if( bangla.charAt(g) == 'ৰ' ) {
                val= 2544;
            }
            else if( bangla.charAt(g)== 'ৱ' ) {
                val= 2545;
            }
            else if( bangla.charAt(g) == 'ঁ' ) {
                val= 2433;
            }
            else if( bangla.charAt(g) == 'ং' ) {
                val= 2434;
            }
            else if( bangla.charAt(g) == 'ঃ' ) {
                val= 2435;
            }
            else if( bangla.charAt(g) == '়' ) {
                val= 2492;
            }
            else if( bangla.charAt(g) == 'া' ) {
                val= 2494;
            }
            else if( bangla.charAt(g) == 'ি' ) {
                val= 2495;
            }
            else if( bangla.charAt(g) == 'ী' ) {
                val= 2496;
            }
            else if( bangla.charAt(g) == 'ু' ) {
                val= 2497;
            }
            else if( bangla.charAt(g) == 'ূ' ) {
                val= 2498;
            }
            else if( bangla.charAt(g) == 'ৃ' ) {
                val= 2499;
            }
            else if( bangla.charAt(g) == 'ৄ' ) {
                val= 2500;
            }
            else if( bangla.charAt(g) == 'ে' ) {
                val= 2503;
            }
            else if(bangla.charAt(g) == 'ৈ' ) {
                val= 2504;
            }
            else if(bangla.charAt(g) == 'ো' ) {
                val= 2507;
            }
            else if( bangla.charAt(g) == 'ৌ' ) {
                val= 2508;
            }
            else if( bangla.charAt(g) == '্' ) {
                val= 2509;
            }
            else if( bangla.charAt(g) == 'ৗ' ) {
                val= 2519;
            }
            else if(bangla.charAt(g) == '০' ) {
                val= 2534;
            }
            else if(bangla.charAt(g) == '১' ) {
                val= 2535;
            }
            else if( bangla.charAt(g) == '২' ) {
                val= 2536;
            }
            else if( bangla.charAt(g) == '৩' ) {
                val= 2537;
            }
            else if( bangla.charAt(g) == '৪' ) {
                val= 2538;
            }
            else if( bangla.charAt(g) == '৫' ) {
                val= 2539;
            }
            else if( bangla.charAt(g) == '৬' ) {
                val= 2540;
            }
            else if( bangla.charAt(g) == '৭' ) {
                val= 2541;
            }
            else if( bangla.charAt(g) == '৮' ) {
                val= 2542;
            }
            else if(bangla.charAt(g) == '৯' ) {
                val= 2543;
            }
//            System.out.print((char)val+" ");
            banglaIC = banglaIC.concat(""+(char)val);

        }
//        System.out.println(">>>>"+banglaIC);

        suggestString = banglaIC;


       /* for(int i=0; i<len_stprint+1; i++) {
//            System.out.println( wordCharValarr[i] ) ;
        }*/

        allSuggestedWOrdsInEng[ totalSuggestedWOrdsCount ]  = stprint;
        allSuggestedWOrds[ totalSuggestedWOrdsCount++ ] = suggestString;

    }

    public static String nearestLetter(char ch) {

        String stNearestLetters="";

        switch (ch) {
            case 'q':
                stNearestLetters="w";  //first line started
                break;
            case 'w':
                stNearestLetters="qe";
                break;
            case 'e':
                stNearestLetters="wr";
                break;
            case 'r':
                stNearestLetters="et";
                break;
            case 't':
                stNearestLetters="ry";
                break;
            case 'y':
                stNearestLetters="tu";
                break;
            case 'u':
                stNearestLetters="yi";
                break;
            case 'i':
                stNearestLetters="uo";
                break;
            case 'o':
                stNearestLetters="ip";
                break;
            case 'p':
                stNearestLetters="o"; //fist line ended
                break;
            case 'a':
                stNearestLetters="s";   // second line started
                break;
            case 's':
                stNearestLetters="ad";
                break;
            case 'd':
                stNearestLetters="sf";
                break;
            case 'f':
                stNearestLetters="dg";
                break;
            case 'g':
                stNearestLetters="fh";
                break;
            case 'h':
                stNearestLetters="gj";
                break;
            case 'j':
                stNearestLetters="hk";
                break;
            case 'k':
                stNearestLetters="jl";
                break;
            case 'l':
                stNearestLetters="k`";  // second line ended
                break;
            case '`':
                stNearestLetters="l";  // second line ended
                break;
            case 'z':
                stNearestLetters="x";      // Third line started
                break;
            case 'x':
                stNearestLetters="zc";
                break;
            case 'c':
                stNearestLetters="xv";
                break;
            case 'v':
                stNearestLetters="cb";
                break;
            case 'b':
                stNearestLetters="vn";
                break;
            case 'n':
                stNearestLetters="bm";
                break;
            case 'm':
                stNearestLetters="n";
                break;
            default:
                stNearestLetters="";
                break;
        }

        return stNearestLetters;
    }

    @Override
    public void onText(CharSequence text) {

        Log.d("onText", "onText");

    }

    @Override
    public void swipeLeft() {

        Log.d("swipeLeft", "swipeLeft");

        Keyboard current = kv.getKeyboard();

        if(current == mEmojiKeyboard)
            setLatinKeyboard(mEmoji2Keyboard);
        else if(current == mEmoji2Keyboard)
            setLatinKeyboard(mEmoji3Keyboard);
        else if(current == mEmoji3Keyboard)
            setLatinKeyboard(mEmoji4Keyboard);
        else if(current == mEmoji4Keyboard)
            setLatinKeyboard(mEmoji5Keyboard);
        else if(current == mEmoji5Keyboard)
            setLatinKeyboard(mEmojiKeyboard);

        setCandidatesViewShown(false);
    }

    @Override
    public void swipeRight() {

        Log.d("swipeRight", "swipeRight");
        Keyboard current = kv.getKeyboard();

        if(current == mEmojiKeyboard)
            setLatinKeyboard(mEmoji5Keyboard);
        else if(current == mEmoji2Keyboard)
            setLatinKeyboard(mEmojiKeyboard);
        else if(current == mEmoji3Keyboard)
            setLatinKeyboard(mEmoji2Keyboard);
        else if(current == mEmoji4Keyboard)
            setLatinKeyboard(mEmoji3Keyboard);
        else if(current == mEmoji5Keyboard)
            setLatinKeyboard(mEmoji4Keyboard);

        setCandidatesViewShown(false);
    }

    @Override
    public void swipeDown() {
        Log.d("swipeDown", "swipeDown");
    }

    @Override
    public void swipeUp() {
        Log.d("swipeUp", "swipeUp");
    }

}