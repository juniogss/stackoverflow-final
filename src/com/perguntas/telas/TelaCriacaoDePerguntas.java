package com.perguntas.telas;

import com.perguntas.crud.CRUD;
import com.perguntas.models.Pergunta;
import com.perguntas.structures.ArvoreBMais;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class TelaCriacaoDePerguntas {

    private static final String PERGUNTAS_PATH = "db/com.perguntas/";
    private final ArvoreBMais arvoreBMais = new ArvoreBMais(44, PERGUNTAS_PATH + "arvore_b.db");
    private final TelaInicioPosLogin telaInicioPosLogin;
    Scanner console = new Scanner(System.in);

    public TelaCriacaoDePerguntas(final TelaInicioPosLogin telaInicioPosLogin) throws IOException {
        this.telaInicioPosLogin = telaInicioPosLogin;
    }

    public void init(int userId) throws Exception {
        CRUD<Pergunta> perguntaCRUD = new CRUD<>(Pergunta.class.getConstructor(), PERGUNTAS_PATH + "com.perguntas.db");

        int option;
        do {
            System.out.println("\n\n-------------------------------");
            System.out.println("        PERGUNTAS 1.0");
            System.out.println("-------------------------------");
            System.out.println("\nINICIO > CRIAÇÃO DE PERGUNTAS");
            System.out.println("\n1) Listar");
            System.out.println("2) Incluir");
            System.out.println("3) Alterar");
            System.out.println("4) Arquivar");
            System.out.println("\n0) Retornar ao menu anterior");
            System.out.print("\nOpção: ");

            try {
                option = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                option = -1;
            }

            switch (option) {
                case 1:
                    int[] idsPerguntas = arvoreBMais.read(userId);

                    int count = 0;
                    if (idsPerguntas.length > 0) {
                        for (int i = 0; i < idsPerguntas.length; i++) {
                            Pergunta pergunta = perguntaCRUD.read(idsPerguntas[i]);

                            if (pergunta != null) {
                                count++;
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");
                                Date date = new Date(pergunta.getCriacao());

                                System.out.println("\n" + (i + 1) + ".");
                                System.out.println(formatter.format(date));
                                System.out.println(pergunta.getPergunta());
                                System.out.println(pergunta.getPalavrasChave());
                            }
                        }
                    }
                    if (count == 0) {
                        System.out.print("Nenhuma pergunta existente.");
                    }
                    break;
                case 2:
                    System.out.println("\nPergunta: ");
                    String pergunta = console.nextLine();

                    if (pergunta.isEmpty()) {
                        break;
                    }

                    System.out.println("\nPalavras chave (separe com ';'): ");
                    String keywords = console.nextLine();

                    if (keywords.isEmpty()) {
                        break;
                    }

                    int idPergunta = perguntaCRUD.create(new Pergunta(userId, pergunta, keywords));
                    arvoreBMais.create(userId, idPergunta);
                    break;
                case 3:
                    //Alterar
                    int[] idsPerguntas3 = arvoreBMais.read(userId);

                    int count3 = 0;
                    if (idsPerguntas3.length > 0) {
                        for (int i = 0; i < idsPerguntas3.length; i++) {
                            Pergunta pergunta3 = perguntaCRUD.read(idsPerguntas3[i]);

                            if (pergunta3 != null) {
                                count3++;
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");
                                Date date = new Date(pergunta3.getCriacao());

                                System.out.println("\n" + (i + 1) + ".");
                                System.out.println(formatter.format(date));
                                System.out.println(pergunta3.getPergunta());
                                System.out.println(pergunta3.getPalavrasChave());
                            }
                        }
                    }
                    if (count3 > 0) {
                        System.out.print("\nQual pergunta deseja alterar? ");
                        int perg;
                        try {
                            perg = Integer.parseInt(console.nextLine());
                        } catch (NumberFormatException e) {
                            perg = 0;
                        }
                        if (perg > 0) {
                            Pergunta perguntaS2 = perguntaCRUD.read(perg);

                            if (perguntaS2 != null) {
                                SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");
                                Date date = new Date(perguntaS2.getCriacao());

                                System.out.println("\n" + (perg) + ".");
                                System.out.println(formatter2.format(date));
                                System.out.println(perguntaS2.getPergunta());
                                System.out.println(perguntaS2.getPalavrasChave());

                                System.out.print("\nRedija a pergunta: ");
                                String perguntaN = console.nextLine();

                                if (perguntaN.isEmpty()) {
                                    continue;
                                }

                                System.out.print("\nRedija as palavras chave: ");
                                String keywordsN = console.nextLine();

                                if (keywordsN.isEmpty()) {
                                    continue;
                                }

                                System.out.print("\nAlterar a pergunta para '" + perguntaN + "' e palavras chave para '" + keywordsN + "'? (1) ");
                                int next;

                                try {
                                    next = Integer.parseInt(console.nextLine());
                                } catch (NumberFormatException e) {
                                    next = -1;
                                }

                                if (next != 1) continue;
                                perguntaS2.setPergunta(perguntaN);
                                perguntaS2.setPalavrasChave(keywordsN);
                                perguntaCRUD.update(perguntaS2);

                                System.out.print("\nPergunta alterada com sucesso.");
                            }
                        }
                    } else {
                        System.out.print("Nenhuma pergunta existente.");
                    }
                    break;
                case 4:
                    //Arquivar
                    int[] idsPerguntas2 = arvoreBMais.read(userId);

                    int count2 = 0;
                    if (idsPerguntas2.length > 0) {
                        for (int i = 0; i < idsPerguntas2.length; i++) {
                            Pergunta pergunta2 = perguntaCRUD.read(idsPerguntas2[i]);

                            if (pergunta2 != null) {
                                count2++;
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");
                                Date date = new Date(pergunta2.getCriacao());

                                System.out.println("\n" + (i + 1) + ".");
                                System.out.println(formatter.format(date));
                                System.out.println(pergunta2.getPergunta());
                                System.out.println(pergunta2.getPalavrasChave());
                            }
                        }
                    }
                    if (count2 > 0) {
                        System.out.print("\nQual pergunta deseja arquivar? ");
                        int perg;
                        try {
                            perg = Integer.parseInt(console.nextLine());
                        } catch (NumberFormatException e) {
                            perg = 0;
                        }
                        if (perg > 0) {
                            Pergunta perguntaS = perguntaCRUD.read(perg);

                            if (perguntaS != null) {
                                SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");
                                Date date = new Date(perguntaS.getCriacao());

                                System.out.println("\n" + (perg) + ".");
                                System.out.println(formatter2.format(date));
                                System.out.println(perguntaS.getPergunta());
                                System.out.println(perguntaS.getPalavrasChave());

                                System.out.print("\nDeseja arquivar esta pergunta? (1) ");
                                int next;

                                try {
                                    next = Integer.parseInt(console.nextLine());
                                } catch (NumberFormatException e) {
                                    next = -1;
                                }

                                if (next != 1) continue;
                                perguntaCRUD.delete(perg);

                                System.out.print("\nPergunta arquivada com sucesso.");
                            }
                        }
                    } else {
                        System.out.print("Nenhuma pergunta existente.");
                    }
                    break;
                case 0:
                    //telaInicioPosLogin.init(userId);
                    break;
                default:
                    System.out.println("Opção inválida ou não implementada");
            }
        } while (option != 0);
    }
}
