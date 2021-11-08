package com.example.background
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.background.databinding.ActivityBlurBinding


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.example.background.databinding.ActivityBlurBinding

    class BlurActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityBlurBinding.inflate(layoutInflater)
            binding.goButton.setOnClickListener { viewModel.applyBlur(blurLevel) }
            binding.cancelButton.setOnClickListener { viewModel.cancelWork() }


            setContentView(binding.root)
            // Setup view output image file button
            binding.seeFileButton.setOnClickListener {
                viewModel.outputUri?.let { currentUri ->
                    val actionView = Intent(Intent.ACTION_VIEW, currentUri)
                    actionView.resolveActivity(packageManager)?.run {
                        startActivity(actionView)
                    }
                }
            }
        }

        private fun workInfosObserver(): Observer<List<WorkInfo>> {
            return Observer { listOfWorkInfo ->


                if (listOfWorkInfo.isNullOrEmpty()) {
                    return@Observer
                }
T
                val workInfo = listOfWorkInfo[0]

                if (workInfo.state.isFinished) {
                    showWorkFinished()

                    val outputImageUri = workInfo.outputData.getString(KEY_IMAGE_URI)

                    // If there is an output file show "See File" button
                    if (!outputImageUri.isNullOrEmpty()) {
                        viewModel.setOutputUri(outputImageUri)
                        binding.seeFileButton.visibility = View.VISIBLE
                    }
                } else {
                    showWorkInProgress()
                }
            }
        }}=