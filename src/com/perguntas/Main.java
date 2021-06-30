package com.perguntas;


import com.perguntas.crud.CRUD;
import com.perguntas.models.Pergunta;
import com.perguntas.models.Resposta;
import com.perguntas.models.User;
import com.perguntas.models.Vote;
import com.perguntas.pcv.PCVEmail;
import com.perguntas.structures.HashExtensivel;
import com.perguntas.telas.TelaInicioPosLogin;

import java.io.File;
import java.util.Scanner;

public class Main {
    private static final String USER_PATH = "db/user/";
    private static final String EMAIL_PATH = "db/email/";
    private static final String PERGUNTAS_PATH = "db/perguntas/";
    private static final String RESPOSTAS_PATH = "db/respostas/";
    private static final String VOTE_PATH = "db/vote/";

    public static void main(String[] args) {

        Scanner console = new Scanner(System.in);

        try {
            File d = new File("db");
            if (!d.exists()) d.mkdir();

            File userFile = new File(USER_PATH);
            if (!userFile.exists()) userFile.mkdir();

            File emailFile = new File(EMAIL_PATH);
            if (!emailFile.exists()) emailFile.mkdir();

            File perguntasFile = new File(PERGUNTAS_PATH);
            if (!perguntasFile.exists()) perguntasFile.mkdir();

            File respostasFile = new File(RESPOSTAS_PATH);
            if (!respostasFile.exists()) respostasFile.mkdir();

            File votesFile = new File(VOTE_PATH);
            if (!votesFile.exists()) votesFile.mkdir();

//            new File(USER_PATH + "user.db").delete();
//            new File(USER_PATH + "hash_d.db").delete();
//            new File(USER_PATH + "hash_c.db").delete();
//            new File(EMAIL_PATH + "hash_c.db").delete();
//            new File(EMAIL_PATH + "hash_d.db").delete();
//            new File(PERGUNTAS_PATH + "perguntas.db").delete();
//            new File(PERGUNTAS_PATH + "arvore_b.db").delete();
//            new File(PERGUNTAS_PATH + "hash_a.db").delete();
//            new File(PERGUNTAS_PATH + "li_a.db").delete();
//            new File(PERGUNTAS_PATH + "li_b.db").delete();
//            new File(RESPOSTAS_PATH + "hash_a.db").delete();
//            new File(RESPOSTAS_PATH + "hash_b.db").delete();
//            new File(RESPOSTAS_PATH + "respostas.db").delete();
//            new File(RESPOSTAS_PATH + "arvRelacaoUserResp.db").delete();
//            new File(RESPOSTAS_PATH + "arvRelacaoPergResp.db").delete();
//            new File(RESPOSTAS_PATH + "arvRelacaoUserPerg.db").delete();
//            new File(VOTE_PATH + "hash_a.db").delete();
//            new File(VOTE_PATH + "hash_b.db").delete();
//            new File(VOTE_PATH + "user_questions.db").delete();
//            new File(VOTE_PATH + "user_answers.db").delete();
//            new File(VOTE_PATH + "votes.db").delete();

            CRUD<User> userCRUD = new CRUD<>(User.class.getConstructor(), USER_PATH + "user.db");
            CRUD<Pergunta> perguntaCRUD = new CRUD<>(Pergunta.class.getConstructor(), PERGUNTAS_PATH + "perguntas.db");
            CRUD<Resposta> respostaCRUD = new CRUD<>(Resposta.class.getConstructor(), RESPOSTAS_PATH + "respostas.db");
            CRUD<Vote> voteCRUD = new CRUD<>(Vote.class.getConstructor(), VOTE_PATH + "votes.db");

            HashExtensivel<PCVEmail> hashEmail = new HashExtensivel<>(PCVEmail.class.getConstructor(), 4, EMAIL_PATH + "hash_d.db", EMAIL_PATH + "hash_c.db");

            int option;
            do {
                System.out.println("\n\n-------------------------------");
                System.out.println("        PERGUNTAS 1.0");
                System.out.println("-------------------------------");
                System.out.println("\nACESSO");
                System.out.println("\n1) Acesso ao sistema");
                System.out.println("2) Novo usuário (primeiro acesso)");
                System.out.println("\n0) Sair");
                System.out.print("\nOpção: ");

                try {
                    option = Integer.parseInt(console.nextLine());
                } catch (NumberFormatException e) {
                    option = -1;
                }

                switch (option) {
                    case 1: {
                        System.out.println("\nACESSO AO SISTEMA");

                        System.out.print("E-mail: ");
                        String email = console.nextLine();

                        PCVEmail pcvEmail = hashEmail.read(email.hashCode());
                        if (pcvEmail == null) {
                            System.out.println("\nUsuário não encontrado!");
                            continue;
                        }

                        int id = Integer.parseInt(pcvEmail.toString().split(";")[1]);

                        User read = userCRUD.read(id);
                        System.out.print("Senha: ");
                        String password = console.nextLine();

                        if (password.equals(read.getPassword())) {
                            TelaInicioPosLogin inicio = new TelaInicioPosLogin(perguntaCRUD, respostaCRUD, voteCRUD);
                            inicio.init(id);
                        } else {
                            System.out.println("\nSenha incorreta!");
                            continue;
                        }
                    }
                    break;
                    case 2: {
                        System.out.println("\nNOVO USUARIO");

                        System.out.print("E-mail: ");
                        String email = console.nextLine();
                        PCVEmail pcvEmail = hashEmail.read(email.hashCode());

                        if (email.isEmpty()) continue;

                        if (pcvEmail != null) {
                            System.out.println("\nEmail já cadastrado!");
                            continue;
                        }

                        System.out.print("Nome: ");
                        String name = console.nextLine();
                        System.out.print("Senha: ");
                        String password = console.nextLine();

                        System.out.println("\nRESUMO");
                        System.out.println("Nome: " + name);
                        System.out.println("Email: " + email);
                        System.out.println("Senha: " + password);
                        System.out.print("\nDeseja concluir o cadastro? (1) ");
                        int next;

                        try {
                            next = Integer.parseInt(console.nextLine());
                        } catch (NumberFormatException e) {
                            next = -1;
                        }

                        if (next != 1) continue;

                        int id = userCRUD.create(new User(name, email, password));
                        hashEmail.create(new PCVEmail(email, id));
                        System.out.println("\nUsuário cadastrado com sucesso!");
                    }
                    break;
                    case 0:
                        break;
                    default:
                        System.out.println("Opção inválida");
                }
            } while (option != 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        console.close();
    }
}