package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.w3c.dom.Text

class ComposeActivity : AppCompatActivity() {
    lateinit var etCompose:EditText
    lateinit var btnTweet: Button
    lateinit var tvCharCount: TextView

    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar=supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_compose)
        etCompose=findViewById(R.id.etTweetCompose)
        btnTweet=findViewById(R.id.btnTweet)
        tvCharCount=findViewById(R.id.tvCharCount)

        client=TwitterApplication.getRestClient(this)

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Fires right as the text is being changed (even supplies the range of text)
                tvCharCount.setText(count.toString())
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                tvCharCount.setText(count.toString())
            }
            override fun afterTextChanged(s: Editable) {
                Log.e(TAG,"Text changed")
            }
        })

        btnTweet.setOnClickListener(){
            val tweetContent=etCompose.text.toString()
            if(tweetContent.isEmpty()){
                Toast.makeText(this,"Empty tweets not allowed!",Toast.LENGTH_SHORT).show()
            }
            if(tweetContent.length>280){
                Toast.makeText(this,"Tweet is too long! Limit is 140 characters",Toast.LENGTH_SHORT).show()
            }
            else {
                client.publishTweet(tweetContent,object:JsonHttpResponseHandler(){
                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG,"Successfully published tweet!")
                        val tweet= Tweet.fromJson(json.jsonObject)
                        val intent= Intent()
                        intent.putExtra("tweet",tweet)
                        setResult(RESULT_OK,intent)
                        finish()
                    }
                    override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?) {
                        Log.e(TAG,"Failed to publish tweet",throwable)
                    }
                })
            }
        }
    }
    companion object{
        var TAG="ComposeActivity"
    }
}