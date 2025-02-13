package br.com.alura.aluraesporte.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.VERTICAL
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import br.com.alura.aluraesporte.R
import br.com.alura.aluraesporte.model.Produto
import br.com.alura.aluraesporte.ui.recyclerview.adapter.ProdutosAdapter
import br.com.alura.aluraesporte.ui.viewmodel.ProdutosViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListaProdutosFragment : Fragment() {

    private val viewModel: ProdutosViewModel by viewModel()
    private val adapter: ProdutosAdapter by inject()
    private lateinit var listaProdutos: RecyclerView
    var quandoProdutoSelecionado: (produto: Produto) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.lista_produtos,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        configuraRecyclerView()
        buscaProdutos()
    }

    private fun buscaProdutos() {
        viewModel.buscaTodos().observe(viewLifecycleOwner) { produtosEncontrados ->
            produtosEncontrados?.let {
                adapter.atualiza(it)
            }
        }
    }

    private fun setupViews(view: View) {
        listaProdutos = view.findViewById(R.id.lista_produtos_recyclerview)
    }

    private fun configuraRecyclerView() {
        val divisor = DividerItemDecoration(context, VERTICAL)
        listaProdutos.addItemDecoration(divisor)
        adapter.onItemClickListener = quandoProdutoSelecionado
        listaProdutos.adapter = adapter
    }

}
