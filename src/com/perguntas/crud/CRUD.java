package com.perguntas.crud;


import com.perguntas.interfaces.Register;
import com.perguntas.models.Pergunta;
import com.perguntas.pcv.PCVPergunta;
import com.perguntas.pcv.PCVResposta;
import com.perguntas.pcv.PCVUser;
import com.perguntas.structures.HashExtensivel;
import com.perguntas.structures.ListaInvertida;

import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

public class CRUD<T extends Register> {

    private static final String USER_PATH = "db/user/";
    private static final String PERGUNTAS_PATH = "db/com.perguntas/";
    private static final String RESPOSTAS_PATH = "db/respostas/";

    private final HashExtensivel<PCVUser> hashUser;
    protected RandomAccessFile file;
    Constructor<T> constructor;
    HashExtensivel<PCVPergunta> hashPergunta;
    HashExtensivel<PCVResposta> hashResposta;
    ListaInvertida listaPalavraChave;

    public CRUD(Constructor<T> constructor, String fileName) throws Exception {
        this.constructor = constructor;
        file = new RandomAccessFile(fileName, "rw");
        file.writeInt(0);
        hashUser = new HashExtensivel<>(PCVUser.class.getConstructor(), 5, USER_PATH + "hash_d.db", USER_PATH + "hash_c.db");
        hashPergunta = new HashExtensivel<>(PCVPergunta.class.getConstructor(), 5, PERGUNTAS_PATH + "hash_a.db", PERGUNTAS_PATH + "hash_b.db");
        hashResposta = new HashExtensivel<>(PCVResposta.class.getConstructor(), 5, RESPOSTAS_PATH + "hash_a.db", RESPOSTAS_PATH + "hash_b.db");
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

        if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.User")) {
            hashUser.create(new PCVUser(object.getID(), pos));
        } else if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.Pergunta")) {
            hashPergunta.create(new PCVPergunta(object.getID(), pos));
            String[] tokens = ((Pergunta) object).getPalavrasChave().split(";");
            for (int i = 0; i < tokens.length; i++) {
                listaPalavraChave.create(tokens[i], object.getID());
            }
        } else if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.Resposta")) {
            hashResposta.create(new PCVResposta(object.getID(), pos));

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
        long position = 0;

        if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.User")) {
            readUser = hashUser.read(id);

            if (readUser == null) return null;
            position = readUser.getPosition();
        } else if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.Pergunta")) {
            readPergunta = hashPergunta.read(id);

            if (readPergunta == null) return null;
            position = readPergunta.getPosition();
        } else if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.Resposta")) {
            readResposta = hashResposta.read(id);

            if (readResposta == null) return null;
            position = readResposta.getPosition();
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
    public boolean delete(int id) throws Exception {
        PCVUser readUser = null;
        PCVPergunta readPergunta = null;
        PCVResposta readResposta;
        long position = 0;

        if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.User")) {
            readUser = hashUser.read(id);
            if (readUser == null) return false;
            position = readUser.getPosition();
        } else if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.Pergunta")) {
            readPergunta = hashPergunta.read(id);
            if (readPergunta == null) return false;
            position = readPergunta.getPosition();
            Pergunta pg = (Pergunta) read(id);
            String[] tokens = ((Pergunta) pg).getPalavrasChave().split(";");
            for (int i = 0; i < tokens.length; i++) {
                listaPalavraChave.delete(tokens[i], id);
            }
        } else if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.Resposta")) {
            readResposta = hashResposta.read(id);
            if (readResposta == null) return false;
            position = readResposta.getPosition();
        }

        if (position == -1) return false;

        file.seek(position);
        file.writeBoolean(false);

        hashUser.delete(id);

        return true;
    }

    /**
     * Atualiza um registro no file, se o tamanho for maior que o antigo,
     * altera o lapide no file, cria um novo registro e atualiza o id no índice
     *
     * @param obj objeto a ser atualizado
     */
    public boolean update(T obj) throws Exception {
        PCVUser readUser = null;
        PCVPergunta readPergunta = null;
        PCVResposta readResposta = null;
        long position = 0;

        if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.User")) {
            readUser = hashUser.read(obj.getID());
            if (readUser == null) return false;
            position = readUser.getPosition();
        } else if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.Pergunta")) {
            readPergunta = hashPergunta.read(obj.getID());
            if (readPergunta == null) return false;
            position = readPergunta.getPosition();
            Pergunta old = (Pergunta) read(obj.getID());
            String[] tokensOld = ((Pergunta) old).getPalavrasChave().split(";");
            for (int i = 0; i < tokensOld.length; i++) {
                listaPalavraChave.delete(tokensOld[i], old.getID());
            }
            String[] tokens = ((Pergunta) obj).getPalavrasChave().split(";");
            for (int i = 0; i < tokens.length; i++) {
                listaPalavraChave.create(tokens[i], obj.getID());
            }
        } else if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.Resposta")) {
            readResposta = hashResposta.read(obj.getID());
            if (readResposta == null) return false;
            position = readResposta.getPosition();
        }

        if (position < 0) return false;

        file.seek(position);
        boolean lap = file.readBoolean();
        int size = file.readShort();
        long lapPosition = file.getFilePointer();
        long objID = file.readInt();

        if (!lap || objID != obj.getID()) return false;

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

            if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.User")) {
                hashUser.update(new PCVUser(obj.getID(), file.getFilePointer()));
            } else if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.Pergunta")) {
                hashPergunta.update(new PCVPergunta(obj.getID(), file.getFilePointer()));
            } else if (constructor.getDeclaringClass().getName().equals("com.com.perguntas.models.Resposta")) {
                hashResposta.update(new PCVResposta(obj.getID(), file.getFilePointer()));
            }

            file.writeBoolean(true);
            file.writeShort(oldObj.length);

        }

        file.write(oldObj);
        return true;
    }
}