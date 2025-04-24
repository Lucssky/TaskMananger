package controller;

import dao.TarefaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Tarefa;
import util.Relogio;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class TarefaController {

    @FXML private TableView<Tarefa> tabelaTarefas;
    @FXML private TableColumn<Tarefa, String> colunaTitulo;
    @FXML private TableColumn<Tarefa, String> colunaStatus;
    @FXML private TableColumn<Tarefa, LocalDate> colunaData;

    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescricao;
    @FXML private DatePicker dateEntrega;
    @FXML private ComboBox<String> comboStatus;
    @FXML private Label lblRelogio;

    private final TarefaDAO tarefaDAO = new TarefaDAO();
    private final ObservableList<Tarefa> tarefas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colunaTitulo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitulo()));
        colunaStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        colunaData.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDataEntrega()));

        comboStatus.getItems().addAll("A Fazer", "Em Andamento", "Concluído");

        carregarTarefas();
        new Relogio(lblRelogio).start();
    }

    private void carregarTarefas() {
        tarefas.clear();
        try {
            tarefas.addAll(tarefaDAO.listar());
            tabelaTarefas.setItems(tarefas);
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar tarefas: " + e.getMessage());
        }
    }

    @FXML
    private void adicionarTarefa() {
        if (validarCampos()) {
            Tarefa tarefa = new Tarefa(
                    txtTitulo.getText(),
                    txtDescricao.getText(),
                    dateEntrega.getValue(),
                    comboStatus.getValue()
            );

            try {
                tarefaDAO.inserir(tarefa);
                limparCampos();
                carregarTarefas();
            } catch (SQLException e) {
                mostrarErro("Erro ao adicionar tarefa: " + e.getMessage());
            }
        }
    }

    @FXML
    private void editarTarefa() {
        Tarefa selecionada = tabelaTarefas.getSelectionModel().getSelectedItem();
        if (selecionada != null && validarCampos()) {
            selecionada.setTitulo(txtTitulo.getText());
            selecionada.setDescricao(txtDescricao.getText());
            selecionada.setDataEntrega(dateEntrega.getValue());
            selecionada.setStatus(comboStatus.getValue());

            try {
                tarefaDAO.atualizar(selecionada);
                limparCampos();
                carregarTarefas();
            } catch (SQLException e) {
                mostrarErro("Erro ao atualizar tarefa: " + e.getMessage());
            }
        } else {
            mostrarErro("Selecione uma tarefa para editar.");
        }
    }

    @FXML
    private void excluirTarefa() {
        Tarefa selecionada = tabelaTarefas.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Exclusão");
            confirm.setHeaderText("Deseja excluir esta tarefa?");
            Optional<ButtonType> resultado = confirm.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    tarefaDAO.remover(selecionada.getId());
                    carregarTarefas();
                } catch (SQLException e) {
                    mostrarErro("Erro ao excluir tarefa: " + e.getMessage());
                }
            }
        } else {
            mostrarErro("Selecione uma tarefa para excluir.");
        }
    }

    @FXML
    private void preencherCampos() {
        Tarefa selecionada = tabelaTarefas.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            txtTitulo.setText(selecionada.getTitulo());
            txtDescricao.setText(selecionada.getDescricao());
            dateEntrega.setValue(selecionada.getDataEntrega());
            comboStatus.setValue(selecionada.getStatus());
        }
    }

    private boolean validarCampos() {
        if (txtTitulo.getText().isEmpty() || dateEntrega.getValue() == null || comboStatus.getValue() == null) {
            mostrarErro("Preencha todos os campos obrigatórios (Título, Data de Entrega e Status).");
            return false;
        }
        return true;
    }

    private void limparCampos() {
        txtTitulo.clear();
        txtDescricao.clear();
        dateEntrega.setValue(null);
        comboStatus.setValue(null);
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}

