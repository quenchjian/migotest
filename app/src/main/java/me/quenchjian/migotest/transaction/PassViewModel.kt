package me.quenchjian.migotest.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.quenchjian.migotest.concurrent.ThreadExecutor
import me.quenchjian.migotest.network.RestfulApi
import me.quenchjian.migotest.network.json.Pass
import javax.inject.Inject

@HiltViewModel
class PassViewModel @Inject constructor(
  private val executor: ThreadExecutor,
  private val api: RestfulApi,
) : ViewModel() {

  val loading: LiveData<Boolean> = MutableLiveData()
  val error: LiveData<Throwable?> = MutableLiveData()
  val status: LiveData<String> = MutableLiveData()
  val added: LiveData<List<PassItem>> = MutableLiveData()
  val subscribed: LiveData<PassItem> = MutableLiveData()

  private val dayPasses = mutableListOf(PassItem(true, Pass.Type.DAY))
  private val hourPasses = mutableListOf(PassItem(true, Pass.Type.HOUR))

  init {
    fetchStatus()
    fetchPass()
  }

  fun fetchStatus() {
    loading as MutableLiveData
    error as MutableLiveData
    status as MutableLiveData
    error.value = null
    executor.io.execute {
      try {
        executor.ui.execute { loading.value = true }
        val status = api.status()
        executor.ui.execute { this.status.value = status }
      } catch (t: Throwable) {
        executor.ui.execute { error.value = t }
      } finally {
        executor.ui.execute { loading.value = false }
      }
    }
  }

  fun fetchPass() {
    loading as MutableLiveData
    error as MutableLiveData
    added as MutableLiveData
    error.value = null
    executor.io.execute {
      try {
        executor.ui.execute { loading.value = true }
        val passes = api.loadPass().map { PassItem(type = it.type, pass = it) }
        dayPasses.addAll(passes.filter { it.type == Pass.Type.DAY })
        hourPasses.addAll(passes.filter { it.type == Pass.Type.HOUR })
        executor.ui.execute { added.value = dayPasses + hourPasses }
      } catch (t: Throwable) {
        executor.ui.execute { error.value = t }
      } finally {
        executor.ui.execute { loading.value = false }
      }
    }
  }

  fun addPass(input: PassInput) {
    loading as MutableLiveData
    error as MutableLiveData
    added as MutableLiveData
    error.value = null
    executor.io.execute {
      try {
        executor.ui.execute { loading.value = true }
        val pass = api.addPass(input.type, input.duration, input.rp)
        when (input.type) {
          Pass.Type.DAY -> dayPasses.add(PassItem(type = input.type, pass = pass))
          Pass.Type.HOUR -> hourPasses.add(PassItem(type = input.type, pass = pass))
        }
        executor.ui.execute { added.value = dayPasses + hourPasses }
      } catch (t: Throwable) {
        executor.ui.execute { error.value = t }
      } finally {
        executor.ui.execute { loading.value = false }
      }
    }
  }

  fun subscribePass(item: PassItem) {
    val pass = item.pass ?: return
    loading as MutableLiveData
    error as MutableLiveData
    subscribed as MutableLiveData
    error.value = null
    executor.io.execute {
      try {
        executor.ui.execute { loading.value = true }
        val subscribedPass = api.subscribe(pass.id)
        val subscribedPassItem = PassItem(type = subscribedPass.type, pass = subscribedPass)
        executor.ui.execute { subscribed.value = subscribedPassItem }
      } catch (t: Throwable) {
        executor.ui.execute { error.value = t }
      } finally {
        executor.ui.execute { loading.value = false }
      }
    }
  }
}