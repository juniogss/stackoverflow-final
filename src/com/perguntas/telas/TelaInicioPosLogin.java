package com.perguntas.telas;

import com.perguntas.crud.CRUD;
import com.perguntas.models.Pergunta;
import com.perguntas.models.Resposta;
import com.perguntas.models.Vote;

import java.io.IOException;
import java.util.Scanner;

public class TelaInicioPosLogin {

    private final CRUD<Pergunta> perguntaCRUD;
    Scanner console = new Scanner(System.in);
    TelaCriacaoDePerguntas criacaoDePerguntas = new TelaCriacaoDePerguntas(this);
    TelaPerguntas perguntas;

    public TelaInicioPosLogin(CRUD<Pergunta> perguntaCRUD, CRUD<Resposta> respostaCRUD, CRUD<Vote> voteCRUD) throws IOException {
        this.perguntaCRUD = perguntaCRUD;
        perguntas = new TelaPerguntas(perguntaCRUD, respostaCRUD, voteCRUD);
    }

    public void init(int id) throws Exception {
        int option;

        do {
            System.out.println("\n\n-------------------------------");
            System.out.println("        PERGUNTAS 1.0");
            System.out.println("-------------------------------");
            System.out.println("\nINICIO");
            System.out.println("\n1) Criação de perguntas");
            System.out.println("2) Consultar/responder perguntas");
            System.out.println("3) Notificações: 0");
            System.out.println("\n0) Sair");
            System.out.print("\nOpção: ");

            try {
                option = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                option = -1;
            }

            switch (option) {
                case 1:
                    criacaoDePerguntas.init(id, perguntaCRUD);
                    break;
                case 2:
                    perguntas.init(perguntaCRUD);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida ou não implementada");
            }
        } while(option != 0);

    }
}
