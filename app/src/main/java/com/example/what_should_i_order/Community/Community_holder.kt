package com.example.what_should_i_order.Community

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.what_should_i_order.Community.Comment.Comment_ListAdapter
import com.example.what_should_i_order.Community.Comment.Comment_ListLayout
import com.example.what_should_i_order.R
import com.example.what_should_i_order.Setting.Setting
import com.example.what_should_i_order.databinding.ActivityCommunityHolderBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_community_holder.*
import java.text.SimpleDateFormat
import java.util.*

class Community_holder : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityHolderBinding

    val db = FirebaseFirestore.getInstance()

    val itemList2 = arrayListOf<Comment_ListLayout>()

    var adapter = Comment_ListAdapter(itemList2, this)

    val itemList = arrayListOf<ListLayout>()








    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_holder)




        binding = ActivityCommunityHolderBinding.inflate(layoutInflater)
        val view = binding.root
        //setContentView(R.layout.activity_cloud_firestore)
        setContentView(view)


        binding.recyclerViewCommunityComment.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewCommunityComment.adapter = adapter

        //

        back.setOnClickListener(View.OnClickListener { finish() })

        intent = intent // ????????? ????????????

        val title = intent.getStringExtra("board_title") //Adapter?????? ?????? ?????? ??????
        val date = intent.getStringExtra("board_date")
        val content = intent.getStringExtra("board_content")
        val holder_doc = intent.getStringExtra("board_doc")
        val password = intent.getStringExtra("board_password")
        val like_count = intent.getStringExtra("board_liked")
        val nickname = intent.getStringExtra("board_Nickname")

        // board_title.setText(title)
        // board_date.setText(content)
        // board_content.setText(date)
        // board_nickname.text = nickname.toString()
        //likes.text = like_count.toString()

        var settings: SharedPreferences = getSharedPreferences("like_tmp", MODE_PRIVATE)

        var editor: SharedPreferences.Editor = settings.edit()

        if(settings.getBoolean(holder_doc.toString(), false))
        {
            notliked.visibility = View.INVISIBLE
            liked.visibility = View.VISIBLE
        }
        else
        {
            notliked.visibility = View.VISIBLE
            liked.visibility = View.INVISIBLE
        }

        db.collection("Contacts")
            .document(holder_doc.toString())
            .get()
            .addOnSuccessListener { result ->
                try {
                    with(result) {
                        board_title.text = "${getString("name")}"
                        board_date.text = "${getString("com_date")}"
                        board_content.text = "${getString("number")}"
                        board_nickname.text = "${getString("nickname")}"
                        likes.text = "${getLong("liked")}"
                        eye_holder_count.text = "????????? : ${getLong("eye_count")}"
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Community_holder, e.toString() , Toast.LENGTH_SHORT).show()
                }
            }



        val go_comment_delelte = Intent(this@Community_holder, Community_holder::class.java)
        go_comment_delelte.putExtra("board_doc", holder_doc)





        //????????????
        db.collection("Contacts")
            .document(holder_doc.toString())
            .collection("Comment")// ????????? ?????????
            .orderBy("Date", Query.Direction.ASCENDING)
            .get() // ?????? ????????????
            .addOnSuccessListener { result ->
                // ????????? ??????
                itemList2.clear()
                for (document in result) {  // ????????? ???????????? result??? ?????????
                    val item2 =
                        Comment_ListLayout(
                            document["Comment"] as String,
                            document["Date"] as String,
                            document["Doc"] as String,
                            document["comment_password"] as String,
                            document["content_doc"] as String)
                    itemList2.add(item2)
                }
                adapter.notifyDataSetChanged()// ??????????????? ??? ??????
            }
            .addOnFailureListener { exception ->
                // ????????? ??????
                Log.w("TAG", "Error getting documents: $exception")
            }

        //

        //????????????

        commnet_button.setOnClickListener {

            val comment_builder = AlertDialog.Builder(this@Community_holder)


            // ??????????????? ????????? ?????? ?????? ??????, ?????? ????????????
            val tvName = TextView(this@Community_holder)
            tvName.text = "\n???????????? ??????\n"

            val password_edit = EditText(this@Community_holder)
            password_edit.isSingleLine = true

            val mLayout = LinearLayout(this@Community_holder)
            mLayout.orientation = LinearLayout.VERTICAL
            mLayout.setPadding(16)
            mLayout.addView(tvName)
            mLayout.addView(password_edit)

            comment_builder.setView(mLayout)

            comment_builder.setTitle("?????? ???????????? ??????")
            comment_builder.setPositiveButton("??????") { dialog, which ->
                // EditText?????? ???????????? ????????? hashMap?????? ??????
                var comment_edit = comment_edit.text.toString()

                val currentTime: Long = System.currentTimeMillis()
                val simpleDate = SimpleDateFormat("yyyy-MM-dd k:mm:ss")
                val mDate: Date = Date(currentTime)
                val getTime = simpleDate.format(mDate)
                val content_doc = holder_doc.toString()

                val doc = UUID.randomUUID().toString()


                val data = hashMapOf(
                    "Comment" to comment_edit,
                    "Date" to getTime.toString(),
                    "Doc" to doc,
                    "comment_password" to password_edit.text.toString(),
                    "content_doc" to content_doc
                )

                db.collection("Contacts")
                    .document(holder_doc.toString())
                    .collection("Comment")
                    .document(doc.toString())
                    .set(data)
                    .addOnSuccessListener {
                        // ????????? ??????
                        Toast.makeText(this, "???????????? ?????????????????????", Toast.LENGTH_SHORT).show()

                        update()
                        //go_board2.putExtra("board_doc", it.toString())
                        // startActivity(go_board2)
                    }
                    .addOnFailureListener { exception ->
                        // ????????? ??????

                        Log.w("MainActivity", "Error getting documents: $exception")
                    }

            }
            comment_builder.setNegativeButton("??????") { dialog, which ->

            }
            comment_builder.show()





        }



        liked.setOnClickListener {
            notliked.visibility = View.VISIBLE
            liked.visibility = View.INVISIBLE

            db.collection("Contacts")
                .document(holder_doc.toString())
                .update("liked", FieldValue.increment(-1))
                .addOnSuccessListener { result ->

                    db.collection("Contacts")
                        .document(holder_doc.toString())
                        .get()
                        .addOnSuccessListener { result ->
                            try {
                                with(result) {

                                    likes.text = "${getLong("liked")}"
                                    editor.remove(holder_doc.toString())
                                    editor.commit()

                                }
                            } catch (e: Exception) {
                                Toast.makeText(this@Community_holder, e.toString() , Toast.LENGTH_SHORT).show()
                            }
                        }


                    Toast.makeText(this@Community_holder, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this@Community_holder, exception.toString(), Toast.LENGTH_SHORT).show()
                }

        }

        notliked.setOnClickListener {
            notliked.visibility = View.INVISIBLE
            liked.visibility = View.VISIBLE
            //????????? ?????? ??????
            db.collection("Contacts")
                .document(holder_doc.toString())
                .update("liked", FieldValue.increment(1))
                .addOnSuccessListener { result ->

                    //


                    db.collection("Contacts")
                        .document(holder_doc.toString())
                        .get()
                        .addOnSuccessListener { result ->
                            try {
                                with(result) {

                                    likes.text = "${getLong("liked")}"
                                    editor.putBoolean(holder_doc.toString(), true)
                                    editor.commit()

                                }
                            } catch (e: Exception) {
                                Toast.makeText(this@Community_holder, e.toString() , Toast.LENGTH_SHORT).show()
                            }
                        }


                    //


                    Toast.makeText(this@Community_holder, "???????????? ???????????????.", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this@Community_holder, exception.toString(), Toast.LENGTH_SHORT).show()
                }
        }

        /*????????? ??????

        db.collection("Contacts")
            .document(holder_doc.toString())
            .get() // ?????? ????????????
            .addOnSuccessListener { result ->
                if (result != null) {
                    likes.text = result.data.toString()
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        */

        content_delete.setOnClickListener {

            val builder = AlertDialog.Builder(this@Community_holder)


            // ??????????????? ????????? ?????? ?????? ??????, ?????? ????????????
            val tvName = TextView(this@Community_holder)
            tvName.text = "\n???????????? ??????\n"

            val password_edit = EditText(this@Community_holder)
            password_edit.isSingleLine = true

            val mLayout = LinearLayout(this@Community_holder)
            mLayout.orientation = LinearLayout.VERTICAL
            mLayout.setPadding(16)
            mLayout.addView(tvName)
            mLayout.addView(password_edit)

            builder.setView(mLayout)

            builder.setTitle("????????? ??????")
            builder.setPositiveButton("??????") { dialog, which ->
                // EditText?????? ???????????? ????????? hashMap?????? ??????

                if(password_edit.text.toString().equals(password.toString())) {
                    db.collection("Contacts")
                        .document(holder_doc.toString())
                        .delete()
                        .addOnSuccessListener {
                            // ????????? ??????
                            Toast.makeText(this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show()

                            finish()
                            //go_board2.putExtra("board_doc", it.toString())
                            // startActivity(go_board2)
                        }
                        .addOnFailureListener { exception ->
                            // ????????? ??????

                            Log.w("MainActivity", "Error getting documents: $exception")
                        }
                } else {
                    Toast.makeText(this, "??????????????? ????????????????????????.", Toast.LENGTH_SHORT).show()
                }



            }
            builder.setNegativeButton("??????") { dialog, which ->

            }
            builder.show()



        }



    }

    fun update() {

        val content_doc = intent.getStringExtra("board_doc")

        db.collection("Contacts")
            .document(content_doc.toString())
            .collection("Comment")// ????????? ?????????
            .orderBy("Date", Query.Direction.ASCENDING)
            .get() // ?????? ????????????
            .addOnSuccessListener { result ->
                // ????????? ??????
                itemList2.clear()
                for (document in result) {  // ????????? ???????????? result??? ?????????
                    val item2 =
                        Comment_ListLayout(
                            document["Comment"] as String,
                            document["Date"] as String,
                            document["Doc"] as String,
                            document["comment_password"] as String,
                            document["content_doc"] as String)
                    itemList2.add(item2)
                }
                adapter.notifyDataSetChanged()// ??????????????? ??? ??????
            }
            .addOnFailureListener { exception ->
                // ????????? ??????
                Log.w("TAG", "Error getting documents: $exception")
            }
    }




}