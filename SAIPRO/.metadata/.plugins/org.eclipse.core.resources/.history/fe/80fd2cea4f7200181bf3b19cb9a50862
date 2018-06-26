package com.up.clinica.model.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import com.up.clinica.model.Animal;
import com.up.clinica.model.Especie;

public class AnimalDAO extends AbstractDAO<Animal, Long> {

	@Override
	protected PreparedStatement criarStatementListar(Connection conexao) throws Exception {
		return conexao.prepareStatement("SELECT a.ID, a.NOME,a.NASCIMENTO,e.id,e.nome,e.descricao "
				+ " FROM animal a INNER JOIN especie e ON a.especie_id = e.id");
	}

	@Override
	protected Animal parseObjeto(ResultSet rs) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Animal a = new Animal();
		a.setId(rs.getLong(1));
		a.setNome(rs.getString(2));
		a.setNascimento(df.parse(rs.getString(3)));
		Especie e = new Especie();
		e.setId(rs.getLong(4));
		e.setNome(rs.getString(5));
		e.setDescricao(rs.getString(6));
		a.setEspecie(e);
		return a;
	}

	@Override
	protected PreparedStatement criarStatementRemover(Connection conexao, Long id) throws Exception {
		PreparedStatement statement = conexao.prepareStatement("DELETE FROM ANIMAL WHERE id=?");
		statement.setLong(1, id);
		return statement;
	}

	@Override
	protected PreparedStatement criarStatementAtualizar(Connection conexao, Animal objeto) throws Exception {
		PreparedStatement statement = conexao
				.prepareStatement("UPDATE animal SET nome=?, nascimento=?,especie_id=? WHERE id=?");
		statement.setString(1, objeto.getNome());
		if (objeto.getNascimento() != null)
			statement.setDate(2, new java.sql.Date(objeto.getNascimento().getTime()));
		else
			statement.setNull(2, java.sql.Types.DATE);
		statement.setLong(3, objeto.getEspecie().getId());
		statement.setLong(4, objeto.getId());

		return statement;
	}

	@Override
	protected void carregarChavesGeradasNoObjeto(ResultSet generatedKeys, Animal objeto) throws Exception {
		objeto.setId(generatedKeys.getLong(1));
	}

	@Override
	protected PreparedStatement criarStatementPersistir(Connection conexao, Animal objeto) throws Exception {
		PreparedStatement statement = conexao.prepareStatement(
				"INSERT INTO ANIMAL (NOME,NASCIMENTO,ESPECIE_ID) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, objeto.getNome());
		if (objeto.getNascimento() != null)
			statement.setDate(2, new java.sql.Date(objeto.getNascimento().getTime()));
		else
			statement.setNull(2, java.sql.Types.DATE);
		statement.setLong(3, objeto.getEspecie().getId());
		return statement;
	}

	@Override
	protected PreparedStatement criarStatementBuscar(Connection conexao, Long id) throws Exception {
		PreparedStatement statement = conexao
				.prepareStatement("SELECT a.ID, a.NOME,a.NASCIMENTO,e.id,e.nome,e.descricao "
						+ " FROM animal a INNER JOIN especie e ON a.especie_id = e.id WHERE a.id = ?");
		statement.setLong(1, id);
		return statement;
	}

	@Override
	protected PreparedStatement criarStatementRemoveComRelacionamentos(Connection conexao, Long id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
