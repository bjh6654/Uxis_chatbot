package com.team2.chatbot_uxis.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.team2.chatbot_uxis.R
import com.team2.chatbot_uxis.msgAdapter
import com.team2.chatbot_uxis.msgItem
import kotlinx.android.synthetic.main.fragment_chatbot.*
import kotlinx.android.synthetic.main.fragment_chatbot.view.*
import java.util.*
import kotlin.collections.ArrayList

class ChatbotFragment : Fragment(),View.OnClickListener {
    lateinit var navController: NavController
    lateinit var database :FirebaseDatabase //채팅 이용을 위한 firebase사용.
    lateinit var chatRef :DatabaseReference

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var msgItemList: ArrayList<msgItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        database = Firebase.database
        chatRef = database.getReference("chat")

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_chatbot, container, false)
        var context = view.context
        msgItemList = ArrayList<msgItem>()

        viewManager = LinearLayoutManager(context)
        viewAdapter = msgAdapter(msgItemList)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            layoutManager =viewManager
            adapter=viewAdapter
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController= Navigation.findNavController(view)
        back_button.setOnClickListener(this)
        send_button.setOnClickListener(this)

        chatRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val msg:msgItem = snapshot.getValue<msgItem>() as msgItem
                msgItemList.add(msg)

                recyclerView.adapter?.notifyDataSetChanged()//이게 될까...


            }
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

        })


    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.back_button->{
                navController.navigate(R.id.action_chatbotFragment_to_mainFragment)
            }
            R.id.send_button->{
                clickSend(v)
            }
        }
    }

    private fun clickSend(v: View) {
        var name = "user"
        var message = et.text.toString()
        var viewType:Int = 0

        var calendar = Calendar.getInstance()
        var time:String = calendar.get(Calendar.HOUR_OF_DAY).toString()+":"+calendar.get(Calendar.MINUTE)

        var msg = msgItem(name,message,viewType)
        chatRef.push().setValue(msg) //database에 방금 입력한 메세지 데이터 (node로) 추가

        et.setText("")
    }

    fun recieve(v:View){
        var name = "chatbot"
        lateinit var message:String //여기에 답변 저장.
        var viewType=1

        var msg = msgItem(name,message,viewType)
        chatRef.push().setValue(msg)

    }
}