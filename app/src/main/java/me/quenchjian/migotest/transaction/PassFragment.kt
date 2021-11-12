package me.quenchjian.migotest.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import me.quenchjian.migotest.R
import me.quenchjian.migotest.databinding.ItemPassBinding
import me.quenchjian.migotest.databinding.ItemPassHeaderBinding
import me.quenchjian.migotest.databinding.ViewPassBinding
import me.quenchjian.migotest.network.json.Pass
import me.quenchjian.migotest.utils.RecyclerListAdapter
import me.quenchjian.migotest.utils.inflate

@AndroidEntryPoint
class PassFragment : Fragment(R.layout.view_pass) {

  private val vm: PassViewModel by lazy {
    ViewModelProvider(this).get(PassViewModel::class.java)
  }
  private var binding: ViewPassBinding? = null
  private val adapter: Adapter get() = binding!!.recyclerPass.adapter as Adapter

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val binding = ViewPassBinding.bind(view).also { this.binding = it }
    binding.recyclerPass.adapter = Adapter()
      .onAddClick { showAddForm(it) }
      .onBuyClick { vm.subscribePass(it) }
      .onItemClick { showDetail(it) }
    binding.recyclerPass.layoutManager = LinearLayoutManager(context)
    vm.loading.observe(viewLifecycleOwner) { binding.layoutLoading.isVisible = it }
    vm.error.observe(viewLifecycleOwner) { error ->
      binding.layoutError.isVisible = error != null
      binding.textError.text = requireContext().getString(R.string.error_message, error?.message)
    }
    vm.status.observe(viewLifecycleOwner) { binding.textResponse.text = it }
    vm.added.observe(viewLifecycleOwner) { adapter.submitList(it) }
    vm.subscribed.observe(viewLifecycleOwner) { adapter.replace(it) }
  }

  override fun onStart() {
    super.onStart()
    vm.fetchStatus()
    vm.fetchPass()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }

  private fun showAddForm(type: Pass.Type) {
    PassFormDialog(requireContext(), type)
      .onAddClick { vm.addPass(it) }
      .show()
  }

  private fun showDetail(item: PassItem) {
    Log.d("Adapter", "isHeader ${item.isHeader} clicked")
    if (item.isHeader) return
    parentFragmentManager.beginTransaction()
      .add(R.id.activity_container, PassDetailFragment.newInstance(item), PassDetailFragment.TAG)
      .hide(this)
      .addToBackStack(null)
      .commit()
  }

  private class Header(root: View) : RecyclerView.ViewHolder(root) {
    private val context = root.context
    private val binding = ItemPassHeaderBinding.bind(root)
    val buttonAdd = binding.buttonAdd

    fun bind(item: PassItem) {
      binding.textPassHeader.text = when (item.type) {
        Pass.Type.DAY -> context.getString(R.string.header_day_pass)
        Pass.Type.HOUR -> context.getString(R.string.header_hour_pass)
      }
    }
  }

  private class Item(root: View) : RecyclerView.ViewHolder(root) {
    private val context = root.context
    private val binding = ItemPassBinding.bind(root)
    val buttonBuy = binding.buttonBuy

    fun bind(item: PassItem) {
      val pass = checkNotNull(item.pass)
      binding.textPassDuration.text = when (item.type) {
        Pass.Type.DAY -> context.getString(R.string.text_day_pass, pass.duration)
        Pass.Type.HOUR -> context.getString(R.string.text_hour_pass, pass.duration)
      }
      binding.textPassRp.text = context.getString(R.string.text_rp, pass.rp)
      binding.buttonBuy.isEnabled = !pass.subscribed
    }
  }

  private class Adapter : RecyclerListAdapter<PassItem, RecyclerView.ViewHolder>(
    itemComparator = { oldItem, newItem -> oldItem.pass?.id == newItem.pass?.id },
    contentComparator = { oldItem, newItem -> oldItem == newItem },
    viewHolderCreator = { parent, viewType ->
      when (viewType) {
        R.layout.item_pass_header -> Header(parent.inflate(viewType))
        R.layout.item_pass -> Item(parent.inflate(viewType))
        else -> throw IllegalArgumentException("Unknown viewType to create")
      }
    }
  ) {
    private var addClick: (Pass.Type) -> Unit = {}
    private var buyClick: (PassItem) -> Unit = {}

    override fun getItemViewType(position: Int): Int {
      return if (getItem(position).isHeader) R.layout.item_pass_header else R.layout.item_pass
    }

    override fun onViewHolderCreated(holder: RecyclerView.ViewHolder) {
      when (holder) {
        is Header -> holder.buttonAdd.setOnClickListener { addClick(getItem(holder.adapterPosition).type) }
        is Item -> holder.buttonBuy.setOnClickListener { buyClick(getItem(holder.adapterPosition)) }
      }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: PassItem) {
      when (holder) {
        is Header -> holder.bind(item)
        is Item -> holder.bind(item)
      }
    }

    fun replace(item: PassItem) {
      val pass = item.pass ?: return
      val index = currentList.indexOfFirst { it.pass?.id == pass.id }
      Log.d("Adapter", "replace index = $index")
      if (index >= 0) {
        val data = currentList.toMutableList()
        data[index] = item
        submitList(data)
      }
    }

    fun onAddClick(click: (Pass.Type) -> Unit) = apply { addClick = click }
    fun onBuyClick(click: (PassItem) -> Unit) = apply { buyClick = click }
  }

  companion object {
    const val TAG = "tag_pass_fragment"
  }
}