package br.com.alura.aluraesporte.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import br.com.alura.aluraesporte.R
import br.com.alura.aluraesporte.extensions.formatParaMoedaBrasileira
import br.com.alura.aluraesporte.model.Produto
import br.com.alura.aluraesporte.ui.activity.CHAVE_PRODUTO_ID
import br.com.alura.aluraesporte.ui.viewmodel.DetalhesProdutoViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetalhesProdutoFragment : Fragment() {

    private lateinit var botaoComprar: Button
    private lateinit var nome: TextView
    private lateinit var preco: TextView
    private val produtoId by lazy {
        arguments?.getLong(CHAVE_PRODUTO_ID)
            ?: throw IllegalArgumentException(ID_PRODUTO_INVALIDO)
    }
    private val viewModel: DetalhesProdutoViewModel by viewModel { parametersOf(produtoId) }
    var quandoProdutoComprado: (produto: Produto) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.detalhes_produto,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        buscaProduto()
        configuraBotaoComprar()
    }

    private fun setupViews(view: View) {
        botaoComprar = view.findViewById(R.id.detalhes_produto_botao_comprar)
        nome = view.findViewById(R.id.detalhes_produto_nome)
        preco = view.findViewById(R.id.detalhes_produto_preco)
    }

    private fun configuraBotaoComprar() {
        botaoComprar.setOnClickListener {
            viewModel.produtoEncontrado.value?.let(quandoProdutoComprado)
        }
    }

    private fun buscaProduto() {
        viewModel.produtoEncontrado.observe(viewLifecycleOwner) {
            it?.let { produto ->
                nome.text = produto.nome
                preco.text = produto.preco.formatParaMoedaBrasileira()
            }
        }
    }

}