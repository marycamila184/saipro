$(document).ready(function() {
	//Carregando a tabela
	setTable();
	
	$("#btn-excluir").click(function(event) {
		var dataRowName = $('tr.selected').attr('data-row-name');

		//Espero a confirmação do usuario
		if (confirm("Deseja deletar a espécie " + dataRowName + "?")) {

			var id = $('tr.selected').attr('id');

			$.ajax({
				url : "/ProjetoClinica/Especies?servico=remover&id=" + id,
				type : "GET",
				dataType: 'text',               
				success : function() {	
					setTable();
					$('#mensagem').html('<div class="alert alert-success" role="alert"><strong>Concluído!</strong> A espécie foi deletada com sucesso.</div>');
				},
				error : function(xhr, textStatus) {
					$('#mensagem').html('<div class="alert alert-danger" role="alert"><strong>Ops!</strong> Ocorreu um erro, tente novamente mais tarde.</div>');
				}
			});
		}
	});
	
	$("#btn-modal-cadastrar").click(function(event) {
		//Altero o titulo da modal
		$('#modal-title').text('Infomações da espécie');
		
		//Limpo os campos
		$('#id').val('0');
		$('#nome').val('');
		$('#descricao').val('');
		$('#tipoanimais').val("-1");	
		
		//Mostro o botão cadastrar e escondo o botão alterar
		$('#btn-alterar').hide();
		$('#btn-cadastrar').show();
		$('#mensagem-modal div').remove();
		$('#mensagem div').remove();
	});
	
	$("#btn-modal-alterar").click(function(event) {
		
		//Altero o titulo da modal
		var id = $('tr.selected').attr('id');
		$('#modal-title').text('Informações da espécie #'+id);
		
		//Mostro o botão alterar e escondo o botão cadastrar
		$('#btn-alterar').show();
		$('#btn-cadastrar').hide();
		
		$('#mensagem-modal div').remove();
		$('#mensagem div').remove();
		
		$('#id').val('0');
		$('#nome').val('');
		$('#descricao').val('');
		$('#tipoanimais').val("-1");	
		
		//Preencho os campos da modal
		$.ajax({
			url : "/ProjetoClinica/Especies?servico=buscar&id=" + id,
			type : "GET",
			success : function(data, textStatus, xhr) {	
				$('#id').val(data.especies.id);
				$('#nome').val(data.especies.nome);
				$('#descricao').val(data.especies.descricao);
				$('#tipoanimais').val(data.especies.tipoAnimal.acronimo);
				$('#myModal').modal('show');				
			},
			error : function(xhr, textStatus) {
				$('#myModal').modal('hide');
				$('#mensagem').html('<div class="alert alert-danger" role="alert"><strong>Ops!</strong> Ocorreu um erro, tente novamente mais tarde.</div>');
			}
		});
	});

	$("#btn-cadastrar").click(function(event) {
		var objeto = {
			id : $('#id').val(),
			nome : $('#nome').val(),
			descricao : $('#descricao').val(),
			tipoAnimal:{
				acronimo : $('#tipoanimais').val()
			}
		}
		
		$.ajax({
			type : "POST",
			url : "/ProjetoClinica/Especies?servico=cadastrar",
			data : JSON.stringify(objeto),
			dataType: 'text',      
			contentType : "application/json; charset=utf-8",
		}).done(function(data, textStatus, jqXHR) {
			$('#myModal').modal('hide');
			$('#mensagem').html('<div class="alert alert-success" role="alert"><strong>Concluído!</strong> A espécie foi cadastrada com sucesso.</div>');
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
	});

	$("#btn-alterar").click(function(event) {
	
		var objeto = {
			id : $('#id').val(),
			nome : $('#nome').val(),
			descricao : $('#descricao').val(),
			tipoAnimal:{
				acronimo : $('#tipoanimais').val()
			}
		}
		
		console.log(JSON.stringify(objeto));

		$.ajax({
			type : "POST",
			url : "/ProjetoClinica/Especies?servico=alterar",
			data : JSON.stringify(objeto),
			dataType: 'text',      
			contentType : "application/json; charset=utf-8",
		}).done(function(data, textStatus, jqXHR) {
			$('#myModal').modal('hide');
			$('#mensagem').html('<div class="alert alert-success" role="alert"><strong>Concluído!</strong> A espécie foi alterada com sucesso.</div>');
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
	});
});

$("#tipoanimais").ready(function(event) {
	$.get("/ProjetoClinica/TipoAnimais?servico=listar", function(data) {
		$("#tipoanimais").append('<option value="-1" disabled selected>Escolha o tipo do animal</option>');
		$.each(data.tipoanimais, function(key, value) {			
			$("#tipoanimais").append("<option value='"+ value.acronimo+"'>"+ value.nome+"</option>");
		});
	});
});

function setTable() {
	$.get("/ProjetoClinica/Especies?servico=listar", function(data) {
		$("#table tbody tr").remove();
		$.each(data.especies, function(key, value) {		
			$('#table tbody').append(
					'<tr id="' + value.id + '" data-row-name="' + value.nome
							+ '"><td>' + value.id + '</td><td>' + value.nome
							+ '</td><td>' + value.descricao + '</td><td>'
							+ value.tipoAnimal.nome + '</td></tr>');
		});
		
		$('#table tr').click(function(event) {
			$('.selected').removeClass('selected');
			$(this).addClass("selected");
			$('#btn-modal-alterar').removeAttr('disabled');
			$('#btn-excluir').removeAttr('disabled');
		});
	});	
}
