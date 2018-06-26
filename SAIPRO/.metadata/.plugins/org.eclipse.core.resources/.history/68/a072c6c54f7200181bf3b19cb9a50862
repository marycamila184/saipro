package com.up.clinica.model.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.up.clinica.model.ConnectionFactory;
import com.up.clinica.model.Especie;
import com.up.clinica.model.TipoAnimal;

public class EspecieDAO extends AbstractDAO<Especie, Long> {

	@Override
	protected void carregarChavesGeradasNoObjeto(ResultSet generatedKeys, Especie objeto) throws Exception {
		objeto.setId(generatedKeys.getLong(1));
	}

	@Override
	protected PreparedStatement criarStatementPersistir(Connection conexao, Especie objeto) throws Exception {
		PreparedStatement statement = conexao.prepareStatement(
				"INSERT INTO ESPECIE (NOME,DESCRICAO,TIPO_ANIMAL_ACRONIMO) VALUES (?,?,?)",
				Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, objeto.getNome());
		statement.setString(2, objeto.getDescricao());
		statement.setString(3, objeto.getTipoAnimal().getAcronimo());
		return statement;
	}

	@Override
	protected PreparedStatement criarStatementListar(Connection conexao) throws Exception {
		return conexao.prepareStatement("SELECT e.ID, e.NOME, e.DESCRICAO, t.ACRONIMO, t.NOME, t.DESCRICAO "
				+ " FROM ESPECIE e INNER JOIN TIPO_ANIMAL t ON e.TIPO_ANIMAL_ACRONIMO = t.ACRONIMO");
	}

	@Override
	protected Especie parseObjeto(ResultSet rs) throws Exception {
		Especie e = new Especie();
		e.setId(rs.getLong(1));
		e.setNome(rs.getString(2));
		e.setDescricao(rs.getString(3));

		TipoAnimal t = new TipoAnimal();
		t.setAcronimo(rs.getString(4));
		t.setNome(rs.getString(5));
		t.setDescricao(rs.getString(6));

		e.setTipoAnimal(t);
		return e;
	}

	@Override
	protected PreparedStatement criarStatementBuscar(Connection conexao, Long id) throws Exception {
		PreparedStatement statement = conexao
				.prepareStatement("SELECT e.ID, e.NOME, e.DESCRICAO, t.ACRONIMO, t.NOME, t.DESCRICAO "
						+ " FROM ESPECIE e INNER JOIN TIPO_ANIMAL t ON e.TIPO_ANIMAL_ACRONIMO = t.ACRONIMO WHERE e.id = ?");
		statement.setLong(1, id);
		return statement;
	}

	@Override
	protected PreparedStatement criarStatementAtualizar(Connection conexao, Especie objeto) throws Exception {
		PreparedStatement statement = conexao
				.prepareStatement("UPDATE especie SET NOME=?, DESCRICAO=?,TIPO_ANIMAL_ACRONIMO=? WHERE id=?");
		statement.setString(1, objeto.getNome());
		statement.setString(2, objeto.getDescricao());
		statement.setString(3, objeto.getTipoAnimal().getAcronimo());
		statement.setLong(4, objeto.getId());

		return statement;
	}

	@Override
	protected PreparedStatement criarStatementRemover(Connection conexao, Long id) throws Exception {
		PreparedStatement statement = conexao.prepareStatement("DELETE FROM ESPECIE WHERE id=?");
		statement.setLong(1, id);
		return statement;
	}

	@Override
	protected PreparedStatement criarStatementRemoveComRelacionamentos(Connection conexao, Long id) throws Exception {
		PreparedStatement statement = conexao.prepareStatement("DELETE FROM ANIMAL WHERE ESPECIE_ID=?");
		statement.setLong(1, id);
		return statement;
	}

	public void removerComRelacionamento(Long idEspecie) throws Exception {
		
	}
}
