package com.perguntas.telas;

import com.perguntas.crud.CRUD;
import com.perguntas.models.Pergunta;
import com.perguntas.models.Resposta;
import com.perguntas.structures.ArvoreBMais;
import com.perguntas.structures.ListaInvertida;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TelaPerguntas {

    private static final String PERGUNTAS_PATH = "db/com.perguntas/";
    private static final String RESPOSTAS_PATH = "db/respostas/";
    private static final Scanner console = new Scanner(System.in);
    private static CRUD<Resposta> arqRespostas;
    private static ArvoreBMais arvoreUR;
    private static ArvoreBMais arvorePR;
    private static ArvoreBMais arvoreUP;

    static {
        try {
            arqRespostas = new CRUD<>(Resposta.class.getConstructor(), RESPOSTAS_PATH + "respostas.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            arvoreUR = new ArvoreBMais(10, RESPOSTAS_PATH + "arvRelacaoUserResp.db");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            arvorePR = new ArvoreBMais(10, RESPOSTAS_PATH + "arvRelacaoPergResp.db");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            arvoreUP = new ArvoreBMais(10, RESPOSTAS_PATH + "arvRelacaoUserPerg.db");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TelaPerguntas() {
    }

    //editado
    public static void respostas(Pergunta p) throws Exception {
        int opcao;

        do {
            System.out.println("\n\n-------------------------------");
            System.out.println("           Respostas");

            System.out.println("\n" + p);
            System.out.println("\n1 - Listar minhas respostas");
            System.out.println("2 - Responder");
            System.out.println("3 - Alterar");
            System.out.println("4 - Arquivar");

            System.out.println("\n0 - Retornar");

            System.out.print("\nOpção: ");

            try {
                opcao = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1: {
                    listaMinhasRespostas(p);
                }
                break;

                case 2: {
                    responder(p);
                }
                break;

                case 3: {
                    alterarResposta(p);
                }
                break;

                case 4: {
                    arquivarResposta(p);
                }
                break;

                case 0:
                    break;

                default: {
                    System.out.println("Opção Inválida!");
                }
            }
        } while (opcao != 0);
    }

    public static int listaRespostas(int[] arrayIdRespostas) throws Exception {
        int cont = 0;

        for (int i = 0; i < arrayIdRespostas.length; i++) {
            Resposta temp = arqRespostas.read(arrayIdRespostas[i]);
            System.out.print("\n" + (i + 1) + ". ");
            System.out.println(temp);

            cont++;
        }

        return cont;
    }

    public static int[] listaMinhasRespostas(int[] arrayIdMinhasRespostas, int idPergunta) throws Exception {
        HashSet<Integer> idMinhasRespostasParaEssaPergunta = new HashSet<>();

        for (int arrayIdMinhasResposta : arrayIdMinhasRespostas) {
            Resposta temp = arqRespostas.read(arrayIdMinhasResposta);
            try {
                if (temp.getPerguntaID() == idPergunta) {
                    idMinhasRespostasParaEssaPergunta.add(temp.getID());
                }
            } catch (NullPointerException e) {
                System.out.println("NullPointerException thrown!");
            }
        }

        int[] conjResposta = new int[idMinhasRespostasParaEssaPergunta.size()];

        int k = 0;

        for (int i : idMinhasRespostasParaEssaPergunta) {
            conjResposta[k++] = i;
        }

        listaRespostas(conjResposta);

        return conjResposta;
    }

    public static void listaMinhasRespostas(Pergunta p) throws Exception {
        int[] arrayIdMinhasRespostas = arvoreUR.read(p.getIdUsuario());

        System.out.println("\n\n-------------------------------");
        System.out.println("Listar Minhas Respostas");

        listaMinhasRespostas(arrayIdMinhasRespostas, p.getID());

        System.out.println("\nPressione qualquer tecla para voltar");
        console.nextLine();
    }

    public static void responder(Pergunta p) throws Exception {
        System.out.println("\n\n-------------------------------");
        System.out.println("Responder");

        System.out.println(p);

        System.out.println("\nDigite sua resposta:");
        String novaResposta = console.nextLine();

        System.out.println("\nConfirmar?:");
        int opcao = confirmar();

        if (opcao == 1) {
            Resposta r = new Resposta(p.getID(), p.getIdUsuario(), novaResposta);
            arqRespostas.create(r);
            arvorePR.create(p.getID(), r.getID());
            arvoreUR.create(p.getIdUsuario(), r.getID());
            System.out.println("Resposta postada!");
        } else {
            System.out.println("Criação cancelada!");
        }
    }

    public static void alterarResposta(Pergunta p) throws Exception {
        int[] arrayIdMinhasRespostas = arvoreUR.read(p.getIdUsuario());
        int opcao;
        System.out.println("\n\n-------------------------------");
        System.out.println("Alterar");

        System.out.println("\n" + p);

        System.out.println("\t\nListando minhas respostas:\n___________________________________");
        arrayIdMinhasRespostas = listaMinhasRespostas(arrayIdMinhasRespostas, p.getID());

        do {
            System.out.print("\nDigite o número da resposta que será alterada (0 para sair): ");

            try {
                opcao = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida!");
                opcao = -1;
            }

            if (opcao <= arrayIdMinhasRespostas.length && opcao >= 1) {

                Resposta temp = arqRespostas.read(arrayIdMinhasRespostas[(opcao - 1)]);
                System.out.print("Digite a alteração que deseja fazer: ");
                String perguntaEditada = console.nextLine();
//                temp.resposta = perguntaEditada;

                Date date = new Date();
                temp.setAlteracao(date.getTime());

                arqRespostas.update(temp);

                System.out.println("Alteração realizada com sucesso!");
            } else {

                if (opcao != 0) {
                    System.out.println("Opção inválida!");
                }
            }

        } while (opcao < 0 || opcao > arrayIdMinhasRespostas.length);

    }

    public static void arquivarResposta(Pergunta p) throws Exception {
        int[] arrayIdMinhasRespostas = arvoreUR.read(p.getIdUsuario());
        int opcao;
        System.out.println("\n\n-------------------------------");
        System.out.println("           Respostas > Arquivar");
        System.out.println("-------------------------------");

        System.out.println("\n" + p);

        System.out.println("\t\nListando minhas respostas:\n___________________________________");
        arrayIdMinhasRespostas = listaMinhasRespostas(arrayIdMinhasRespostas, p.getID());

        do {
            System.out.print("\nDigite o número da resposta que será arquivada (0 para sair): ");

            try {
                opcao = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida!");
                opcao = -1;
            }

            if (opcao <= arrayIdMinhasRespostas.length && opcao >= 1) {

                Resposta temp = arqRespostas.read(arrayIdMinhasRespostas[(opcao - 1)]);
                System.out.println("\nConfirmar?");
                opcao = confirmar();

                if (opcao == 1) {
                    Date date = new Date();
                    temp.setAtiva(false);
                    temp.setAlteracao(date.getTime());
                    arqRespostas.update(temp);
                    arvoreUR.delete(p.getIdUsuario(), temp.getID());
                    arvorePR.delete(p.getID(), temp.getID());
                    System.out.println("Resposta arquivada!");
                } else {
                    System.out.println("Arquivamento cancelado!");

                }

            } else {
                if (opcao != 0) {
                    System.out.println("Opção inválida!");
                }
            }

        } while (opcao < 0 || opcao > arrayIdMinhasRespostas.length);

    }

    public static int confirmar() throws InterruptedException {
        int opcao;
        System.out.println("1 - Sim");
        System.out.println("2 - Não");
        System.out.print("\nOpção: ");

        try {
            opcao = Integer.parseInt(console.nextLine());
        } catch (NumberFormatException e) {
            opcao = 2;
            System.out.println("Opção inválida!");
        }
        return opcao;
    }

    public void init() throws Exception {
        ListaInvertida listaPalavraChave = new ListaInvertida(4, PERGUNTAS_PATH + "li_a.db", PERGUNTAS_PATH + "li_b.db");
        CRUD<Pergunta> perguntaCRUD = new CRUD<>(Pergunta.class.getConstructor(), PERGUNTAS_PATH + "com.perguntas.db");

        String option;
        int nOption;

        do {
            System.out.println("\n\n-------------------------------");
            System.out.println("        PERGUNTAS 1.0");
            System.out.println("-------------------------------");
            System.out.println("\nINICIO > PERGUNTAS");
            System.out.println("\nBusque as com.perguntas por palavas chave separadas por ponto e virgula");
            System.out.print("\nPalavras chave: ");

            option = console.nextLine();
            try {
                nOption = Integer.parseInt(option);
            } catch (NumberFormatException e) {
                nOption = -1;
            }

            String[] tokens = option.split(";");
            Integer[] ids = new Integer[0];
            for (int i = 0; i < tokens.length; i++) {
                if (i == 0) {
                    ids = Arrays.stream(listaPalavraChave.read(tokens[i])).boxed().toArray(Integer[]::new);
                } else {
                    Set<Integer> s1 = new HashSet<>(Arrays.asList(ids));
                    Set<Integer> s2 = new HashSet<>(Arrays.asList(Arrays.stream(listaPalavraChave.read(tokens[i])).boxed().toArray(Integer[]::new)));
                    s1.retainAll(s2);

                    ids = s1.toArray(new Integer[s1.size()]);
                }
            }
            //lista de ids
            ArrayList<Pergunta> pgt = new ArrayList<>();
            for (Integer id : ids) {
                pgt.add(perguntaCRUD.read(id));
            }
            //lista de objetos, ordena
            pgt.sort(Comparator.comparingInt(Pergunta::getNota));
            //mostrar com.perguntas
            int count = 0;
            for (int i = 0; i < ((List<Pergunta>) pgt).size(); i++) {
                Pergunta pergunta = ((List<Pergunta>) pgt).get(i);

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
            //prompt
            if (count > 0) {
                System.out.print("\nQual pergunta deseja visualizar? ");
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

                        System.out.println("\nRESPOSTAS");
                        System.out.println("---------");
                        System.out.println("\n1) Responder");
                        System.out.println("\n2) Avaliar");
                        System.out.println("\n0) Retornar");
                        System.out.print("\nOpção: ");

                        int nOpt;
                        do {
                            try {
                                nOpt = Integer.parseInt(console.nextLine());
                            } catch (NumberFormatException e) {
                                nOpt = -1;
                            }

                            switch (nOpt) {
                                case 0:
                                    break;
                                case 1:
                                    respostas(perguntaS2);
                                default:
                                    System.out.println("Opção inválida ou não implementada");
                            }
                        } while (nOpt != 0);
                    }
                }
            } else {
                System.out.print("Nenhuma pergunta existente.");
            }
        } while (nOption != 0);

    }
}
