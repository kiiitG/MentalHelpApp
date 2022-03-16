package com.example.mainscreenlayout.ui.chat

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mainscreenlayout.CommandAdapter
import com.example.mainscreenlayout.databinding.ChatFragmentBinding
import com.example.mainscreenlayout.model.Message
import com.example.mainscreenlayout.utils.QueryUtils
import java.lang.IllegalStateException

class ChatFragment : Fragment() {

    companion object {
        fun newInstance(id : String) : ChatFragment {
            val instance = ChatFragment()
            val args = Bundle()
            args.putString("id", id)
            instance.arguments = args
            return instance
        }
    }

    private var chatViewManager = LinearLayoutManager(context)
    private var commandChatViewManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: ChatFragmentBinding

    private val chatAdapter: ChatAdapter = ChatAdapter(listOf())
    private val commandAdapter: CommandAdapter = CommandAdapter(listOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = ChatFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //todo catch illegal state exc in require argumanets
        try {
            val k = requireArguments().getString("id", "default")
            viewModel = ViewModelProvider(this, ChatViewModelFactory(k)).get(ChatViewModel::class.java)
        } catch (e : IllegalStateException) {
            viewModel = ViewModelProvider(this, ChatViewModelFactory("mock")).get(ChatViewModel::class.java)
        }
        // set chat recycler view
        binding.recyclerChat.layoutManager = chatViewManager
        binding.recyclerChat.adapter = chatAdapter
        viewModel.observeMessages(viewLifecycleOwner, {
            chatAdapter.addItem(it)
            //chatAdapter.setItems(it)
        })

        // set command recycler view
        binding.recyclerChatCommand.layoutManager = commandChatViewManager
        binding.recyclerChatCommand.adapter = commandAdapter
        viewModel.observeCommands(viewLifecycleOwner, {
            commandAdapter.setItems(it)
        })

        // set command button on click listener
        commandAdapter.onItemClick = {
            viewModel.processCommand(it)
        }

        // set send button on click listener
        binding.buttonChatSend.setOnClickListener {
            val message = binding.editGchatMessage.text.toString()
            viewModel.processMessage(Message(message, "me", 0))
        }
    }
}