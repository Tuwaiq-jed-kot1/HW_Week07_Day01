package com.example.background.workers


import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.background.IMAGE_MANIPULATION_WORK_NAME
import com.example.background.KEY_IMAGE_URI
import com.example.background.TAG_OUTPUT


class BlurViewModel(application: Application) : ViewModel() {

    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null
    private val workManager = WorkManager.getInstance(application)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    init {

        outputWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
        imageUri = getImageUri(application.applicationContext)
    }

    internal fun applyBlur(blurLevel: Int) {}

    internal fun cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        imageUri?.let {
            builder.putString(KEY_IMAGE_URI, imageUri.toString())
        }
        return builder.build()
    }

    internal fun applyBlur(blurLevel: Int) {

        var continuation = workManager
            .beginUniqueWork(
                IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker::class.java)
            )


        for (i in 0 until blurLevel) {
            val blurBuilder = OneTimeWorkRequestBuilder<BlurWorker>()


            if (i == 0) {
                blurBuilder.setInputData(createInputDataForUri())
            }

            continuation = continuation.then(blurBuilder.build())
        }

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        val save = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
            .setConstraints(constraints)
            .addTag(TAG_OUTPUT)
            .build()
        continuation = continuation.then(save)


        continuation.enqueue()
    }



    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            class BlurViewModel(application: Application) : ViewModel() {
                internal fun setOutputUri(outputImageUri: String?) {
                    outputUri = uriOrNull(outputImageUri)
                }
            }

            class BlurViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
                class BlurViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

                    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                        return if (modelClass.isAssignableFrom(BlurViewModel::class.java)) {
                            BlurViewModel(application) as T
                        } else {
                            throw IllegalArgumentException("Unknown ViewModel class")
                        }
                        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                            return if (modelClass.isAssignableFrom(BlurViewModel::class.java)) {
                                BlurViewModel(application) as T
                            } else {
                                throw IllegalArgumentException("Unknown ViewModel class")
                            }
                        }
                    }
                }

