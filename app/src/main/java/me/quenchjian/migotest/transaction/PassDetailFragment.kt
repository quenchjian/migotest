package me.quenchjian.migotest.transaction

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import me.quenchjian.migotest.R
import me.quenchjian.migotest.databinding.ViewPassDetailBinding
import me.quenchjian.migotest.network.json.Pass
import me.quenchjian.migotest.utils.hide
import java.time.format.DateTimeFormatterBuilder

class PassDetailFragment : Fragment(R.layout.view_pass_detail) {

  private val formatter = DateTimeFormatterBuilder()
    .appendPattern("yyyy/MM/dd HH:mm:ss")
    .toFormatter()
  private var binding: ViewPassDetailBinding? = null

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val pass = item.pass ?: return
    val ctx = requireContext()
    val binding = ViewPassDetailBinding.bind(view).also { this.binding = it }
    binding.textPassName.text = when (pass.type) {
      Pass.Type.DAY -> ctx.getString(R.string.text_day_pass, pass.duration)
      Pass.Type.HOUR -> ctx.getString(R.string.text_hour_pass, pass.duration)
    }
    val status = when {
      item.isExpired() -> ctx.getString(R.string.text_pass_status_expired)
      pass.subscribed -> ctx.getString(R.string.text_pass_status_activate)
      else -> ctx.getString(R.string.text_pass_status_inactivate)
    }
    binding.textPassStatus.text = ctx.getString(R.string.text_pass_status, status)
    binding.textPassCreate.text = ctx.getString(R.string.text_pass_create, pass.createDate.format(formatter))
    if (pass.subscribed) {
      binding.textPassStart.text = ctx.getString(R.string.text_pass_start, pass.start.format(formatter))
      binding.textPassEnd.text = ctx.getString(R.string.text_pass_end, pass.end.format(formatter))
    } else {
      binding.textPassStart.hide()
      binding.textPassEnd.hide()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }

  companion object {

    const val TAG = "tag_pass_detail_fragment"

    private var PassDetailFragment.item: PassItem
      get() = checkNotNull(requireArguments().getParcelable("arg_pass_item"))
      set(value) {
        arguments = bundleOf("arg_pass_item" to value)
      }

    fun newInstance(item: PassItem): PassDetailFragment {
      return PassDetailFragment().also { it.item = item }
    }
  }
}