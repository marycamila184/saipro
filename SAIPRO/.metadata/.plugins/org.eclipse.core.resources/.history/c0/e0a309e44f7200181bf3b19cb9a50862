package com.up.clinica.model.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.up.clinica.model.ConnectionFactory;

public abstract class AbstractDAO<T, U> implements IGenericDAO<T, U> {

	@Override
	public List<T> listar() throws Exception {
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		List<T> retorno = new ArrayList<T>();
		Exception ultimaExcecao = null;

		try {
			con = ConnectionFactory.getConnection();
			con.setAutoCommit(false);
			statement = this.criarStatementListar(con);
			rs = statement.executeQuery();

			while (rs.next()) {
				retorno.add(this.parseObjeto(rs));
			}

			con.commit();
			return retorno;

		} catch (Exception e) {
			ultimaExcecao = e;
			con.rollback();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				ultimaExcecao = e;
			}
			try {
				if (statement != null)
					statement.close();
			} catch (Exception e) {
				ultimaExcecao = e;
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				ultimaExcecao = e;
			}
		}
		throw ultimaExcecao;
	}

	@Override
	public void persistir(T objeto) throws Exception {
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet generatedKeys = null;
		Exception ultimaExcecao = null;

		try {
			con = ConnectionFactory.getConnection();
			con.setAutoCommit(false);
			statement = this.criarStatementPersistir(con, objeto);
			statement.executeUpdate();

			generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {
				this.carregarChavesGeradasNoObjeto(generatedKeys, objeto);
			}

			con.commit();
			return;
		} catch (Exception e) {
			ultimaExcecao = e;
			con.rollback();
		} finally {
			try {
				if (generatedKeys != null)
					generatedKeys.close();
			} catch (SQLException e) {
				ultimaExcecao = e;
			}
			try {
				if (statement != null)
					statement.close();
			} catch (Exception e) {
				ultimaExcecao = e;
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				ultimaExcecao = e;
			}
		}
		throw ultimaExcecao;
	}

	@Override
	public T buscar(U id) throws Exception {
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		T retorno = null;
		Exception ultimaExcecao = null;

		try {
			con = ConnectionFactory.getConnection();
			con.setAutoCommit(false);
			statement = this.criarStatementBuscar(con, id);
			rs = statement.executeQuery();

			if (rs.next())
				retorno = this.parseObjeto(rs);

			con.commit();
			return retorno;
		} catch (Exception e) {
			ultimaExcecao = e;
			con.rollback();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				ultimaExcecao = e;
			}
			try {
				if (statement != null)
					statement.close();
			} catch (Exception e) {
				ultimaExcecao = e;
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				ultimaExcecao = e;
			}
		}
		throw ultimaExcecao;
	}

	@Override
	public void atualizar(T objeto) throws Exception {
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet generatedKeys = null;
		Exception ultimaExcecao = null;

		try {
			con = ConnectionFactory.getConnection();
			con.setAutoCommit(false);
			statement = this.criarStatementAtualizar(con, objeto);
			statement.executeUpdate();
			con.commit();
			return;
		} catch (Exception e) {
			ultimaExcecao = e;
			con.rollback();
		} finally {
			try {
				if (generatedKeys != null)
					generatedKeys.close();
			} catch (SQLException e) {
				ultimaExcecao = e;
			}
			try {
				if (statement != null)
					statement.close();
			} catch (Exception e) {
				ultimaExcecao = e;
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				ultimaExcecao = e;
			}
		}
		throw ultimaExcecao;
	}

	@Override
	public void remover(U id) throws Exception {
		Connection con = null;
		PreparedStatement statementRelacionamentos = null;
		PreparedStatement statement = null;
		ResultSet generatedKeys = null;
		Exception ultimaExcecao = null;

		try {
			con = ConnectionFactory.getConnection();
			con.setAutoCommit(false);
			
			if(this.criarStatementRemoveComRelacionamentos(con, id) != null){
				statementRelacionamentos = this.criarStatementRemoveComRelacionamentos(con, id);
				statementRelacionamentos.executeUpdate();
			}		
			
			statement = this.criarStatementRemover(con, id);
			statement.executeUpdate();
			con.commit();
			return;
		} catch (Exception e) {
			ultimaExcecao = e;
			con.rollback();
		} finally {
			try {
				if (generatedKeys != null)
					generatedKeys.close();
			} catch (SQLException e) {
				ultimaExcecao = e;
			}
			try {
				if (statement != null)
					statement.close();
				
				if (statementRelacionamentos != null)
					statementRelacionamentos.close();
			} catch (Exception e) {
				ultimaExcecao = e;
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				ultimaExcecao = e;
			}
		}
		throw ultimaExcecao;
	}
	
	protected abstract PreparedStatement criarStatementBuscar(Connection conexao, U id) throws Exception;

	protected abstract PreparedStatement criarStatementRemover(Connection conexao, U id) throws Exception;
	
	protected abstract PreparedStatement criarStatementRemoveComRelacionamentos(Connection conexao, U id) throws Exception;

	protected abstract void carregarChavesGeradasNoObjeto(ResultSet generatedKeys, T objeto) throws Exception;

	protected abstract PreparedStatement criarStatementPersistir(Connection conexao, T objeto) throws Exception;

	protected abstract PreparedStatement criarStatementAtualizar(Connection conexao, T objeto) throws Exception;

	protected abstract PreparedStatement criarStatementListar(Connection conexao) throws Exception;

	protected abstract T parseObjeto(ResultSet rs) throws Exception;
}
