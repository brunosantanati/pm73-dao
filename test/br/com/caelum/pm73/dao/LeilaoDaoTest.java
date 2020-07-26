package br.com.caelum.pm73.dao;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;

import static org.junit.Assert.*;

public class LeilaoDaoTest {
	
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
    public void deveTrazerLeiloesNaoEncerradosNoPeriodo() {

        // criando as datas
        Calendar comecoDoIntervalo = Calendar.getInstance();
        comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
        Calendar fimDoIntervalo = Calendar.getInstance();
        Calendar dataDoLeilao1 = Calendar.getInstance();
        dataDoLeilao1.add(Calendar.DAY_OF_MONTH, -2);
        Calendar dataDoLeilao2 = Calendar.getInstance();
        dataDoLeilao2.add(Calendar.DAY_OF_MONTH, -20);

        Usuario mauricio = new Usuario("Mauricio Aniche",
                "mauricio@aniche.com.br");

        // criando os leiloes, cada um com uma data
        Leilao leilao1 = 
                new Leilao("XBox", 700.0, mauricio, false);
        leilao1.setDataAbertura(dataDoLeilao1);
        Leilao leilao2 = 
                new Leilao("Geladeira", 1700.0, mauricio, false);
        leilao2.setDataAbertura(dataDoLeilao2);

        // persistindo os objetos no banco
        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(leilao1);
        leilaoDao.salvar(leilao2);

        // invocando o metodo para testar
        List<Leilao> leiloes = 
                leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);

        // garantindo que a query funcionou
        assertEquals(1, leiloes.size());
        assertEquals("XBox", leiloes.get(0).getNome());
    }

}
