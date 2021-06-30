package com.perguntas.crud;


import com.perguntas.interfaces.Register;
import com.perguntas.models.Pergunta;
import com.perguntas.pcv.PCVPergunta;
import com.perguntas.pcv.PCVResposta;
import com.perguntas.pcv.PCVUser;
import com.perguntas.pcv.PCVVote;
import com.perguntas.structures.HashExtensivel;
import com.perguntas.structures.ListaInvertida;

import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.Arrays;

public class CRUD<T extends Register> {

    private static final String USER_PATH = "db/user/";
    private static final String PERGUNTAS_PATH = "db/perguntas/";
    private static final String RESPOSTAS_PATH = "db/respostas/";
    private static final String VOTE_PATH = "db/vote/";

    private final HashExtensivel<PCVUser> hashUser;
    protected RandomAccessFile file;
    Constructor<T> constructor;
    HashExtensivel<PCVPergunta> hashPergunta;
    HashExtensivel<PCVResposta> hashResposta;
    HashExtensivel<PCVVote> hashVote;
    ListaInvertida listaPalavraChave;

    public CRUD(Constructor<T> constructor, String fileName) throws Exception {
        this.constructor = constructor;
        file = new RandomAccessFile(fileName, "rw");
        file.writeInt(0);

        hashUser = new HashExtensivel<>(PCVUser.class.getConstructor(), 5, USER_PATH + "hash_d.db", USER_PATH + "hash_c.db");
        hashPergunta = new HashExtensivel<>(PCVPergunta.class.getConstructor(), 5, PERGUNTAS_PATH + "hash_a.db", PERGUNTAS_PATH + "hash_b.db");
        hashResposta = new HashExtensivel<>(PCVResposta.class.getConstructor(), 5, RESPOSTAS_PATH + "hash_a.db", RESPOSTAS_PATH + "hash_b.db");
        hashVote = new HashExtensivel<>(PCVVote.class.getConstructor(), 5, VOTE_PATH + "hash_a.db", VOTE_PATH + "hash_b.db");
        listaPalavraChave = new ListaInvertida(4, PERGUNTAS_PATH + "li_a.db", PERGUNTAS_PATH + "li_b.db");
    }

    /**
     * Cria um objeto no file e adiciona o ID no indice
     *
     * @param object objeto
     * @return id do objeto criado
     */
    public int create(T object) throws Exception {

        file.seek(0);
        object.setID(file.readInt() + 1);

        file.seek(0);
        file.writeInt(object.getID());

        file.seek(file.length());
        long pos = file.getFilePointer();

        byte[] reg = object.toByteArray();

        file.writeBoolean(true);
        file.writeShort(reg.length);
        file.write(reg);

        file.seek(0);
        System.out.println(file.readInt());
        System.out.println(object.getID());
        System.out.println(Arrays.toString(reg));
        System.out.println(pos);

        switch (constructor.getDeclaringClass().getName()) {
            case "com.perguntas.models.User":
                hashUser.create(new PCVUser(object.getID(), pos));
                break;
            case "com.perguntas.models.Pergunta":
                hashPergunta.create(new PCVPergunta(object.getID(), pos));
                String[] tokens = ((Pergunta) object).getPalavrasChave().split(";");
                for (String token : tokens)
                    listaPalavraChave.create(token, object.getID());
                break;
            case "com.perguntas.models.Resposta":
                hashResposta.create(new PCVResposta(object.getID(), pos));
                break;
            case "com.perguntas.models.Vote":
                System.out.println(object);
                hashVote.create(new PCVVote(object.getID(), pos));
                break;
        }



        return object.getID();
    }

    /**
     * Lê um registro do file de acordo com o índice
     *
     * @param id identificador do registro
     * @return objeto do registro
     */
    public T read(int id) throws Exception {

        PCVUser readUser;
        PCVPergunta readPergunta;
        PCVResposta readResposta;
        PCVVote readVote;
        long position = 0;

        switch (constructor.getDeclaringClass().getName()) {
            case "com.perguntas.models.User":
                readUser = hashUser.read(id);

                if (readUser == null) return null;
                position = readUser.getPosition();
                break;
            case "com.perguntas.models.Pergunta":
                readPergunta = hashPergunta.read(id);

                if (readPergunta == null) return null;
                position = readPergunta.getPosition();
                break;
            case "com.perguntas.models.Resposta":
                readResposta = hashResposta.read(id);

                if (readResposta == null) return null;
                position = readResposta.getPosition();
                break;
            case "com.perguntas.models.Vote":
                readVote = hashVote.read(id);

                if (readVote == null) return null;
                position = readVote.getPosition();
                break;
        }

        if (position < 0) return null;

        file.seek(position);
        boolean lap = file.readBoolean();
        int size = file.readShort();
        long lapPosition = file.getFilePointer();
        long objID = file.readInt();

        if (!lap || objID != id) return null;

        byte[] reg = new byte[size];
        file.seek(lapPosition);
        file.read(reg);

        T obj = constructor.newInstance();
        obj.fromByteArray(reg);

        return obj;
    }

    /**
     * "Apaga" um registro do file colocando uma flag <code>false</code> no lápide
     * e deleta a chave do índice
     *
     * @param id identificador do registro
     */
    public void delete(int id) throws Exception {
        PCVUser readUser;
        PCVPergunta readPergunta;
        PCVResposta readResposta;
        PCVVote readVote;
        long position = 0;

        switch (constructor.getDeclaringClass().getName()) {
            case "com.perguntas.models.User":
                readUser = hashUser.read(id);
                if (readUser == null) return;
                position = readUser.getPosition();
                break;
            case "com.perguntas.models.Pergunta":
                readPergunta = hashPergunta.read(id);
                if (readPergunta == null) return;
                position = readPergunta.getPosition();
                Pergunta pg = (Pergunta) read(id);
                String[] tokens = pg.getPalavrasChave().split(";");
                for (String token : tokens)
                    listaPalavraChave.delete(token, id);
                break;
            case "com.perguntas.models.Resposta":
                readResposta = hashResposta.read(id);
                if (readResposta == null) return;
                position = readResposta.getPosition();
                break;
            case "com.perguntas.models.Vote":
                readVote = hashVote.read(id);
                if (readVote == null) return;
                position = readVote.getPosition();
                break;
        }

        if (position == -1) return;

        file.seek(position);
        file.writeBoolean(false);

        hashUser.delete(id);

    }

    /**
     * Atualiza um registro no file, se o tamanho for maior que o antigo,
     * altera o lapide no file, cria um novo registro e atualiza o id no índice
     *
     * @param obj objeto a ser atualizado
     */
    public void update(T obj) throws Exception {
        PCVUser readUser;
        PCVPergunta readPergunta;
        PCVResposta readResposta;
        PCVVote readVote;
        long position = 0;

        switch (constructor.getDeclaringClass().getName()) {
            case "com.perguntas.models.User":
                readUser = hashUser.read(obj.getID());
                if (readUser == null) return;
                position = readUser.getPosition();
                break;
            case "com.perguntas.models.Pergunta":
                readPergunta = hashPergunta.read(obj.getID());
                if (readPergunta == null) return;
                position = readPergunta.getPosition();
                Pergunta old = (Pergunta) read(obj.getID());
                String[] tokensOld = old.getPalavrasChave().split(";");
                for (String s : tokensOld)
                    listaPalavraChave.delete(s, old.getID());

                String[] tokens = ((Pergunta) obj).getPalavrasChave().split(";");
                for (String token : tokens)
                    listaPalavraChave.create(token, obj.getID());
                break;
            case "com.perguntas.models.Resposta":
                readResposta = hashResposta.read(obj.getID());
                if (readResposta == null) return;
                position = readResposta.getPosition();
                break;
            case "com.perguntas.models.Vote":
                readVote = hashVote.read(obj.getID());
                if (readVote == null) return;
                position = readVote.getPosition();
                break;
        }

        if (position < 0) return;

        file.seek(position);
        boolean lap = file.readBoolean();
        int size = file.readShort();
        long lapPosition = file.getFilePointer();
        long objID = file.readInt();

        if (!lap || objID != obj.getID()) return;

        byte[] reg = new byte[size];
        file.seek(lapPosition);
        file.read(reg);

        byte[] oldObj = obj.toByteArray();
        file.seek(position);

        if (oldObj.length <= size) {

            file.writeBoolean(true);
            file.writeShort(size);

        } else {

            file.writeBoolean(false);
            file.seek(file.length());

            switch (constructor.getDeclaringClass().getName()) {
                case "com.perguntas.models.User":
                    hashUser.update(new PCVUser(obj.getID(), file.getFilePointer()));
                    break;
                case "com.perguntas.models.Pergunta":
                    hashPergunta.update(new PCVPergunta(obj.getID(), file.getFilePointer()));
                    break;
                case "com.perguntas.models.Resposta":
                    hashResposta.update(new PCVResposta(obj.getID(), file.getFilePointer()));
                    break;
                case "com.perguntas.models.Vote":
                    hashVote.update(new PCVVote(obj.getID(), file.getFilePointer()));
                    break;
            }

            file.writeBoolean(true);
            file.writeShort(oldObj.length);
        }

        file.write(oldObj);
    }
}