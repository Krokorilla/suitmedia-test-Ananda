package com.example.suitmediatest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.suitmediatest.databinding.ActivityThirdBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ThirdActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThirdBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var userListAdapter: UserListAdapter
    private val client = OkHttpClient()

    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Third Screen"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.rv_user)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userListAdapter = UserListAdapter { user ->
            val resultIntent = Intent()
            resultIntent.putExtra("selectedUserName", "${user.firstName} ${user.lastName}")
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        recyclerView.adapter = userListAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            currentPage = 1
            isLastPage = false
            fetchUserList()
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (!isLoading && !isLastPage && totalItemCount <= lastVisibleItem + 5) {
                    currentPage++
                    fetchUserList()
                }
            }
        })

        fetchUserList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchUserList() {
        isLoading = true
        val url = "https://reqres.in/api/users?page=$currentPage&per_page=10"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@ThirdActivity,
                        "Failed to fetch users: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.swipeRefreshLayout.isRefreshing = false
                    isLoading = false
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { body ->
                    val jsonObject = JSONObject(body.string())
                    val dataArray = jsonObject.getJSONArray("data")
                    val userList = mutableListOf<User>()
                    for (i in 0 until dataArray.length()) {
                        val user = dataArray.getJSONObject(i)
                        val userObj = User(
                            id = user.getInt("id"),
                            firstName = user.getString("first_name"),
                            lastName = user.getString("last_name"),
                            email = user.getString("email"),
                            avatar = user.getString("avatar")
                        )
                        userList.add(userObj)
                    }
                    runOnUiThread {
                        if (currentPage == 1) {
                            userListAdapter.submitList(userList)
                        } else {
                            userListAdapter.appendList(userList)
                        }
                        binding.emptyState.visibility = if (userList.isEmpty()) View.VISIBLE else View.GONE
                        isLastPage = dataArray.length() < 10
                        isLoading = false
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        })
    }
}