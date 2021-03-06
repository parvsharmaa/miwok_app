package com.example.android.miwok;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ColorsActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    //handles audio focus when playing a sound file
    private AudioManager mAudioManager;

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListerner =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        mMediaPlayer.pause();
                        mMediaPlayer.seekTo(0);
                        // pause playback
                    }
                    else if(focusChange == AudioManager.AUDIOFOCUS_GAIN){
                        //the AUDIOFOCUS GAIN case means we have regained focus and can
                        //resume playback
                        mMediaPlayer.start();
                    }
                    else if(focusChange == AudioManager.AUDIOFOCUS_LOSS){
                        //the AUDIO_LOSS case manes we've lost audio focus and
                        // stop playback and clean up resources
                        releaseMediaPlayer();
                    }
                }
            };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.word_list);

            //create and setup the link to request audio focus
            mAudioManager = (AudioManager)getSystemService(getApplicationContext().AUDIO_SERVICE);

           final  ArrayList<Word> words = new ArrayList<Word>();
//        words.add("one");

            words.add(new Word("red","wetetti",R.drawable.color_red,R.raw.color_red));
            words.add(new Word("green","chokokki",R.drawable.color_green,R.raw.color_green));
            words.add(new Word("brown","takaakki",R.drawable.color_brown,R.raw.color_brown));
            words.add(new Word("gray","topoppi",R.drawable.color_gray,R.raw.color_gray));
            words.add(new Word("black","kululli",R.drawable.color_black,R.raw.color_black));
            words.add(new Word("white","kelelli",R.drawable.color_white,R.raw.color_white));
            words.add(new Word("dusty yellow","ṭopiisә",R.drawable.color_dusty_yellow,R.raw.color_dusty_yellow));
            words.add(new Word("mustard yellow","chiwiiṭә",R.drawable.color_mustard_yellow,R.raw.color_mustard_yellow));

            /*
             Create an {@link ArrayAdapter}, whose data source is a list of Strings. The
             adapter knows how to create layouts for each item in the list, using the
             simple_list_item_1.xml layout resource defined in the Android framework.
             This list item layout contains a single {@link TextView}, which the adapter will set to
             display a single word.
            */
            WordAdapter adapter;
            adapter = new WordAdapter(this, words, R.color.category_colors);

            // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
            // There should be a {@link ListView} with the view ID called list, which is declared in the
            // word_list.xml layout file.
            ListView listView = (ListView) findViewById(R.id.list);

            // Make the {@link ListView} use the {@link ArrayAdapter} we created above, so that the
            // {@link ListView} will display list items for each word in the list of words.
            // Do this by calling the setAdapter method on the {@link ListView} object and pass in
            // 1 argument, which is the {@link ArrayAdapter} with the variable name itemsAdapter.
            listView.setAdapter(adapter);
///             FOR SOUND
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Word word = words.get(position);

                    // release the media player if it currently exists because we are about to
                    //play a different sound file.
                    releaseMediaPlayer();

                    //request audio focus for playback
                    int results = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListerner,
                            //use the music stream
                            AudioManager.STREAM_MUSIC,
                            //request permanent focus.
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                    if (results == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        //we have a audio focus now

                        mMediaPlayer = MediaPlayer.create(ColorsActivity.this, word.getmAudioResourceId());
                        mMediaPlayer.start();
                        // setup a listener on the media player, so that we can stop or release the
                        // media player once the sound has finished
                        mMediaPlayer.setOnCompletionListener(mCompletionListener);
//               (if you want to display roast message) Toast.makeText(NumbersActivity.this, "List item is clicked", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }
    @Override
    protected void onStop(){
        super.onStop();
        //when the activity is stopped, release the media player resources because we won't
        //be playing any more sounds.
        releaseMediaPlayer();
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            //regardless of whether or not we were granted audio focus, abandon it. This alse
            // unregisters the AudioFocusChangeListerner so we don't get anymore callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListerner);
        }
    }
}