package br.com.alura.aluraesporte.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import br.com.alura.aluraesporte.R
import br.com.alura.aluraesporte.extensions.formatParaMoedaBrasileira
import br.com.alura.aluraesporte.model.Pagamento
import br.com.alura.aluraesporte.model.Produto
import br.com.alura.aluraesporte.ui.activity.CHAVE_PRODUTO_ID
import br.com.alura.aluraesporte.ui.viewmodel.PagamentoViewModel
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val FALHA_AO_CRIAR_PAGAMENTO = "Falha ao criar pagamento"

class PagamentoFragment : Fragment() {

    private val produtoId by lazy {
        arguments?.getLong(CHAVE_PRODUTO_ID)
            ?: throw IllegalArgumentException(ID_PRODUTO_INVALIDO)
    }
    private val viewModel: PagamentoViewModel by viewModel()
    private lateinit var produtoEscolhido: Produto
    private lateinit var preco: TextView
    private lateinit var botaoConfirmarPagamento: Button
    private lateinit var numeroCartao: TextInputLayout
    private lateinit var dataValidade: TextInputLayout
    private lateinit var cvc: TextInputLayout
    var quandoPagamentoRealizado: (idPagamento: Long) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.pagamento,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        configuraBotaoConfirmaPagamento()
        buscaProduto()
    }

    private fun setupViews(view: View) {
        preco = view.findViewById(R.id.pagamento_preco)
        botaoConfirmarPagamento = view.findViewById(R.id.pagamento_botao_confirma_pagamento)
        numeroCartao = view.findViewById(R.id.pagamento_numero_cartao)
        dataValidade = view.findViewById(R.id.pagamento_data_validade)
        cvc = view.findViewById(R.id.pagamento_cvc)
    }

    private fun buscaProduto() {
        viewModel.buscaProdutoPorId(produtoId).observe(viewLifecycleOwner) {
            it?.let { produtoEncontrado ->
                produtoEscolhido = produtoEncontrado
                preco.text = produtoEncontrado.preco
                    .formatParaMoedaBrasileira()
            }
        }
    }

    private fun configuraBotaoConfirmaPagamento() {
        botaoConfirmarPagamento.setOnClickListener {
            criaPagamento()?.let(this::salva) ?: Toast.makeText(
                context,
                FALHA_AO_CRIAR_PAGAMENTO,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun salva(pagamento: Pagamento) {
        if (::produtoEscolhido.isInitialized) {
            viewModel.salva(pagamento)
                .observe(this, Observer {
                    it?.dado?.let(quandoPagamentoRealizado)
                })
        }
    }

    private fun criaPagamento(): Pagamento? {
        val numeroCartao = numeroCartao.editText?.text.toString()
        val dataValidade = dataValidade.editText?.text.toString()
        val cvc = cvc.editText?.text.toString()
        return geraPagamento(numeroCartao, dataValidade, cvc)
    }

    private fun geraPagamento(
        numeroCartao: String,
        dataValidade: String,
        cvc: String
    ): Pagamento? = try {
        Pagamento(
            numeroCartao = numeroCartao.toInt(),
            dataValidade = dataValidade,
            cvc = cvc.toInt(),
            produtoId = produtoId,
            preco = produtoEscolhido.preco
        )
    } catch (e: NumberFormatException) {
        null
    }

}