$(document).ready(function() {
	//Carregando a tabela
	setTable();
	
	$("#btn-excluir").click(function(event) {
		var dataRowName = $('tr.selected').attr('data-row-name');

		//Espero a confirmação do usuario
		if (confirm("Deseja deletar o tipo animal " + dataRowName + "?")) {

			var id = $('tr.selected').attr('id');

			$.ajax({
				url : "/ProjetoClinica/TipoAnimais?servico=remover&id=" + id,
				type : "GET",
				dataType: 'text',               
				success : function() {	
					setTable();
					$('#mensagem').html('<div class="alert alert-success" role="alert"><strong>Concluído!</strong> O tipo animal foi deletado com sucesso.</div>');
				},
				error : function(xhr, textStatus) {
					$('#mensagem').html('<div class="alert alert-danger" role="alert"><strong>Ops!</strong> Ocorreu um erro, tente novamente mais tarde.</div>');
				}
			});
		}
	});
	
	$("#btn-modal-cadastrar").click(function(event) {
		//Altero o titulo da modal
		$('#modal-title').text('Infomações do tipo animal');
		
		//Limpo os campos
		$('#acronimo').val('');
		$('#nome').val('');
		$('#descricao').val('');
		
		//Mostro o botão cadastrar e escondo o botão alterar
		$('#btn-alterar').hide();
		$('#btn-cadastrar').show();
		$('#mensagem-modal div').remove();
	
	});
	
	$("#btn-modal-alterar").click(function(event) {
		
		//Altero o titulo da modal
		var id = $('tr.selected').attr('id');
		$('#modal-title').text('Informações do tipo animal #'+id);
		
		//Mostro o botão alterar e escondo o botão cadastrar
		$('#btn-alterar').show();
		$('#btn-cadastrar').hide();
		
		$('#mensagem-modal div').remove();
		
		$('#acronimo').val('');
		$('#nome').val('');
		$('#descricao').val('');
		
		//Preencho os campos da modal
		$.ajax({
			url : "/ProjetoClinica/TipoAnimais?servico=buscar&id=" + id,
			type : "GET",
			success : function(data, textStatus, xhr) {	
				$('#acronimo').val(data.tipoanimais.acronimo);
				$('#nome').val(data.tipoanimais.nome);
				var descricao = '';
				if (typeof data.tipoanimais.descricao != 'undefined'){
					descricao = data.tipoanimais.descricao;
				}
				$('#descricao').val(descricao);
				$('#myModal').modal('show');				
			},
			error : function(xhr, textStatus) {
				$('#myModal').modal('hide');
				$('#mensagem').html('<div class="alert alert-danger" role="alert"><strong>Ops!</strong> Ocorreu um erro, tente novamente mais tarde.</div>');
			}
		});
	});

	$("#btn-cadastrar").click(function(event) {
		
		if($('#acronimo').val().length > 3){
			$('#mensagem-modal').html('<div class="alert alert-danger" role="alert"><strong>Ops!</strong> O acrônimo possui no máximo 3 caracteres.</div>');
			$('#acronimo').val('');
		}else{
			var objeto = {
				acronimo : $('#acronimo').val(),
				nome : $('#nome').val(),
				descricao :   $('#descricao').val()
			}
	
			$.ajax({
				type : "POST",
				url : "/ProjetoClinica/TipoAnimais?servico=cadastrar",
				data : JSON.stringify(objeto),
				contentType : "application/json; charset=utf-8",
				dataType: 'text',      
			}).done(function(data, textStatus, jqXHR) {
				$('#myModal').modal('hide');
				$('#mensagem').html('<div class="alert alert-success" role="alert"><strong>Concluído!</strong> O tipo de animal foi cadastrado com sucesso.</div>');
				setTable();		
				$('#btn-modal-alterar').attr("disabled", true);
				$('#btn-excluir').attr("disabled", true);
	        }).fail(function(jqXHR, textStatus, errorThrown) {
	        	if(jqXHR.status == 400){
	        		$('#mensagem-modal').html('<div class="alert alert-danger" role="alert"><strong>Ops!</strong> Preencha todos os campos corretamente.</div>');
	        	}else{
	        		$('#mensagem-modal').html('<div class="alert alert-danger" role="alert"><strong>Ops!</strong> Ocorreu um erro, tente novamente mais tarde.</div>');
	        	}
	        });
		}
	});

	$("#btn-alterar").click(function(event) {
		if($('#acronimo').val().length > 3){
			$('#mensagem-modal').html('<div class="alert alert-danger" role="alert"><strong>Ops!</strong> O acrônimo possui no máximo 3 caracteres.</div>');
			$('#acronimo').val('');
		}else{
		
			var objeto = {
				acronimo : $('#acronimo').val(),
				nome : $('#nome').val(),
				descricao :   $('#descricao').val()
			}
	
			$.ajax({
				type : "POST",
				url : "/ProjetoClinica/TipoAnimais?servico=alterar",
				data : JSON.stringify(objeto),
				dataType: 'text',      
				contentType : "application/json; charset=utf-8",
			}).done(function(data, textStatus, jqXHR) {
				$('#myModal').modal('hide');
				$('#mensagem').html('<div class="alert alert-success" role="alert"><strong>Concluído!</strong> O tipo de animal foi alterado com sucesso.</div>');
				setTable();		
				$('#btn-modal-alterar').attr("disabled", true);
				$('#btn-excluir').attr("disabled", true);
	        }).fail(function(jqXHR, textStatus, errorThrown) {
	        	if(jqXHR.status == 400){
	        		$('#mensagem-modal').html('<div class="alert alert-danger" role="alert"><strong>Ops!</strong> Preencha todos os campos corretamente.</div>');
	        	}else{
	        		$('#mensagem-modal').html('<div class="alert alert-danger" role="alert"><strong>Ops!</strong> Ocorreu um erro, tente novamente mais tarde.</div>');
	        	}
	        });
		}
	});
});


function setTable() {
	$.get("/ProjetoClinica/TipoAnimais?servico=listar", function(data) {
		$("#table tbody tr").remove();
		$.each(data.tipoanimais, function(key, value) {		
			var descricao = '';
			if (typeof value.descricao != 'undefined'){
				descricao = value.descricao;
			}
			$('#table tbody').append(
					'<tr id="' + value.acronimo + '" data-row-name="' + value.nome
							+ '"><td>' + value.acronimo + '</td><td>' + value.nome
							+ '</td><td>' + descricao + '</td></tr>');
		});
		
		$('#table tr').click(function(event) {
			$('.selected').removeClass('selected');
			$(this).addClass("selected");
			$('#btn-modal-alterar').removeAttr('disabled');
			$('#btn-excluir').removeAttr('disabled');
		});
	});	
}
