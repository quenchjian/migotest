package me.quenchjian.migotest.transaction

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import me.quenchjian.migotest.R
import me.quenchjian.migotest.databinding.FormPassBinding
import me.quenchjian.migotest.network.json.Pass

class PassFormDialog(
  context: Context,
  private val type: Pass.Type,
) : Dialog(context, R.style.Theme_Dialog) {

  private var addClick: (PassInput) -> Unit = {}
  private val binding: FormPassBinding

  init {
    setContentView(R.layout.form_pass)
    binding = FormPassBinding.bind(findViewById(R.id.form_pass))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
  }

  override fun onStart() {
    super.onStart()
    binding.textPassTitle.text = when (type) {
      Pass.Type.DAY -> context.getString(R.string.form_pass_title_day)
      Pass.Type.HOUR -> context.getString(R.string.form_pass_title_hour)
    }
    binding.buttonAdd.setOnClickListener {
      binding.editlayoutDuration.error = null
      binding.editlayoutRp.error = null
      val duration = binding.editDuration.text?.toString() ?: ""
      val rp = binding.editRp.text?.toString() ?: ""
      binding.editlayoutDuration.error = duration.check(R.string.error_duration_empty) { it.isEmpty() }
        ?: duration.check(R.string.error_duration_invalid) { it.toIntOrNull() == null }
      binding.editlayoutRp.error = rp.check(R.string.error_rp_empty) { it.isEmpty() }
        ?: rp.check(R.string.error_rp_invalid) { it.toDoubleOrNull() == null }
      if (binding.editlayoutDuration.error == null && binding.editlayoutRp.error == null) {
        dismiss()
        addClick(PassInput(type, duration.toInt(), rp.toDouble()))
      }
    }
    binding.buttonCancel.setOnClickListener { dismiss() }
  }

  override fun onStop() {
    super.onStop()
    binding.buttonAdd.setOnClickListener(null)
    binding.buttonCancel.setOnClickListener(null)
  }

  fun onAddClick(click: (PassInput) -> Unit) = apply { addClick = click }

  private fun checkDuration(error: Int, value: String): String? {
    val hasError = when (error) {
      R.string.error_duration_empty -> value.isEmpty()
      R.string.error_duration_invalid -> value.toIntOrNull() == null
      else -> false
    }
    if (hasError) {
      return context.getString(error)
    }
    return null
  }

  private fun checkRp(error: Int, value: String): String? {
    val hasError = when (error) {
      R.string.error_rp_empty -> value.isEmpty()
      R.string.error_rp_invalid -> value.toDoubleOrNull() == null
      else -> false
    }
    if (hasError) {
      return context.getString(error)
    }
    return null
  }

  private fun String.check(error: Int, check: (String) -> Boolean): String? {
    if (check(this)) {
      return context.getString(error)
    }
    return null
  }
}