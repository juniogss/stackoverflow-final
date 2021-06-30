package com.perguntas.telas;

import com.perguntas.crud.CRUD;
import com.perguntas.models.Pergunta;
import com.perguntas.models.Resposta;
import com.perguntas.models.Vote;
import com.perguntas.structures.ArvoreBMais;

import java.io.IOException;
import java.util.Scanner;

public class VoteScreen {

    private static final String VOTE_PATH = "db/vote/";
    private static final String QUESTION_PATH = "db/perguntas/";
    private static final String ANSWER_PATH = "db/respostas/";
    private final CRUD<Pergunta> questionCRUD;
    private final CRUD<Resposta> answerCRUD;
    private final CRUD<Vote> voteCRUD;

    Scanner console = new Scanner(System.in);

    public VoteScreen(CRUD<Pergunta> questionCRUD, CRUD<Resposta> answerCRUD, CRUD<Vote> voteCRUD)  {
        this.questionCRUD = questionCRUD;
        this.answerCRUD = answerCRUD;
        this.voteCRUD = voteCRUD;
    }

    public void init(int userId, int idQuestion) throws Exception {
        ArvoreBMais arvoreUserVoteQuestions = new ArvoreBMais(10, VOTE_PATH + "user_questions.db");
        ArvoreBMais arvoreUserVoteAnswers = new ArvoreBMais(10, VOTE_PATH + "user_answers.db");
        ArvoreBMais arvoreAnswers = new ArvoreBMais(10, ANSWER_PATH + "arvRelacaoPergResp.db");


        int option;
        do {
            System.out.println("\n\n-------------------------------");
            System.out.println("        PERGUNTAS 1.0");
            System.out.println("-------------------------------");
            System.out.println("\nINICIO > PERGUNTAS > AVALIAR");
            System.out.println("\n1) Votar na pergunta");
            System.out.println("2) Votar na resposta");
            System.out.println("\n0) Retornar ao menu anterior");
            System.out.print("\nOpção: ");

            try {
                option = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                option = -1;
            }

            switch (option) {
                case 1:

                    int[] questions = arvoreUserVoteQuestions.read(userId);

                    for (int question : questions)
                        if (question == idQuestion) {
                            System.out.print("\nVocê já votou nessa pergunta!");
//                            new TelaPerguntas().init();
                            return;
                        }

                    Pergunta question = questionCRUD.read(idQuestion);

                    if (question != null) {

                        System.out.print("\nQual o seu voto? Positivo (1) ou Negativo (Qualquer dígito)?");
                        int vote;

                        try {
                            vote = Integer.parseInt(console.nextLine());
                        } catch (NumberFormatException e) {
                            vote = -1;
                        }

                        System.out.print("\nConfirmar o voto? (1) ");
                        int next;

                        try {
                            next = Integer.parseInt(console.nextLine());
                        } catch (NumberFormatException e) {
                            next = -1;
                        }

                        if (next != 1) continue;

                        voteCRUD.create(new Vote(userId, "P", idQuestion, vote == 1));
                        arvoreUserVoteQuestions.create(userId, idQuestion);

                        question.setNota((short) 5);
                        questionCRUD.update(question);

                        System.out.print("\nVoto confirmado com sucesso.");

//                        new TelaPerguntas().init();
                    }

                    break;
                case 2:

                    int[] answers = arvoreAnswers.read(idQuestion);

                    for (int i = 0; i < answers.length; i++) {
                        Resposta answer = answerCRUD.read(answers[i]);
                        System.out.print("\n" + (i + 1) + ". ");
                        System.out.println(answer);
                    }

                    System.out.print("\nInforme o número da resposta que deseja votar: ");

                    int answerNumber;

                    try {
                        answerNumber = Integer.parseInt(console.nextLine());
                    } catch (NumberFormatException e) {
                        answerNumber = -1;
                    }

                    if (answerNumber <= 0 || answerNumber > answers.length)
                        continue;

                    Resposta answer = answerCRUD.read(answers[answerNumber - 1]);

                    if (answer != null) {

                        int[] userAnswers = arvoreUserVoteAnswers.read(userId);

                        for (int i : userAnswers)
                            if (i == answer.getID()) {
                                System.out.print("\nVocê já votou nessa resposta!");
//                                new TelaPerguntas().init();
                                return;
                            }

                        System.out.print("\nQual o seu voto? Positivo (1) ou Negativo (Qualquer dígito)?");
                        int vote;

                        try {
                            vote = Integer.parseInt(console.nextLine());
                        } catch (NumberFormatException e) {
                            vote = -1;
                        }

                        System.out.print("\nConfirmar o voto? (1) ");
                        int next;

                        try {
                            next = Integer.parseInt(console.nextLine());
                        } catch (NumberFormatException e) {
                            next = -1;
                        }

                        if (next != 1) continue;

                        voteCRUD.create(new Vote(userId, "R", answer.getID(), vote == 1));
                        arvoreUserVoteAnswers.create(userId, answer.getID());

                        answer.setNota((short) 5);
                        answerCRUD.update(answer);

                        System.out.print("\nVoto confirmado com sucesso.");

//                        new TelaPerguntas().init();
                    }

                    break;
                case 0:
//                    new TelaInicioPosLogin().init(userId);

                    break;
                default:
                    System.out.println("Opção inválida ou não implementada");
            }
        } while (option != 0);
    }
}
