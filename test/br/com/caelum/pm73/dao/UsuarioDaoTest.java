package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;

public class UsuarioDaoTest {
	
	private Session session;
    private LeilaoDao leilaoDao;
    private UsuarioDao usuarioDao;
    
    @Before
    public void antes() {
        session = new CriadorDeSessao().getSession();
        leilaoDao = new LeilaoDao(session);
        usuarioDao = new UsuarioDao(session);

        // inicia transacao
        session.beginTransaction();
    }
    
    @After
    public void depois() {
        // faz o rollback
        session.getTransaction().rollback();
        session.close();
    }
	
	@Test
    public void deveEncontrarPeloNomeEEmail() {
        Usuario novoUsuario = new Usuario
                ("João da Silva", "joao@dasilva.com.br");
        usuarioDao.salvar(novoUsuario);

        Usuario usuarioDoBanco = usuarioDao
                .porNomeEEmail("João da Silva", "joao@dasilva.com.br");

        assertEquals("João da Silva", usuarioDoBanco.getNome());
        assertEquals("joao@dasilva.com.br", usuarioDoBanco.getEmail());
    }
	
	@Test
	public void deveRetornarNuloSeNaoEncontrarUsuario() {
		Usuario usuarioDoBanco = usuarioDao
				.porNomeEEmail("João Joaquim", "joao@joaquim.com.br");

		assertNull(usuarioDoBanco);
	}
	
	@Test
    public void deveContarLeiloesNaoEncerrados() {
        // criamos um usuario
        Usuario mauricio = 
                new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

        // criamos os dois leiloes
        Leilao ativo = 
                new Leilao("Geladeira", 1500.0, mauricio, false);
        Leilao encerrado = 
                new Leilao("XBox", 700.0, mauricio, false);
        encerrado.encerra();

        // persistimos todos no banco
        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(ativo);
        leilaoDao.salvar(encerrado);

        // pedimos o total para o DAO
        long total = leilaoDao.total();

        assertEquals(1L, total);
    }
	
	@Test
	public void deveRetornarZeroSeNaoHaLeiloesNovos() {
		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		Leilao encerrado = new Leilao("XBox", 700.0, mauricio, false);
		Leilao tambemEncerrado = new Leilao("Geladeira", 1500.0, mauricio, false);
		encerrado.encerra();
		tambemEncerrado.encerra();

		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(encerrado);
		leilaoDao.salvar(tambemEncerrado);

		long total = leilaoDao.total();

		assertEquals(0L, total);
	}

}
