package com.perguntas.models;

import com.perguntas.interfaces.Register;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Pergunta implements Register {

    private int idPergunta;
    private int idUsuario;
    private long criacao;
    private short nota;
    private String pergunta;
    private boolean ativa;
    private String palavrasChave;

    public Pergunta() {
        this.idPergunta = -1;
        this.idUsuario = -1;
        this.criacao = -1;
        this.nota = -1;
        this.pergunta = "";
        this.ativa = false;
        this.palavrasChave = "";
    }

    public Pergunta(int idUsuario, String pergunta, String palavrasChave) {
        this.idPergunta = -1;
        this.idUsuario = idUsuario;
        this.criacao = System.currentTimeMillis();
        this.nota = 0;
        this.pergunta = pergunta;
        this.ativa = true;
        this.palavrasChave = palavrasChave;
    }

    @Override
    public int getID() {
        return idPergunta;
    }

    @Override
    public void setID(int idPergunta) {
        this.idPergunta = idPergunta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public long getCriacao() {
        return criacao;
    }

    public void setCriacao(long criacao) {
        this.criacao = criacao;
    }

    public short getNota() {
        return nota;
    }

    public void setNota(short nota) {
        this.nota = nota;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public String getPalavrasChave() {
        return palavrasChave;
    }

    public void setPalavrasChave(String pc) {
        this.palavrasChave = pc;
    }

    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idPergunta);
        dos.writeInt(idUsuario);
        dos.writeLong(criacao);
        dos.writeShort(nota);
        dos.writeUTF(pergunta);
        dos.writeBoolean(ativa);
        dos.writeUTF(palavrasChave);
        dos.close();
        baos.close();

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idPergunta = dis.readInt();
        idUsuario = dis.readInt();
        criacao = dis.readLong();
        nota = dis.readShort();
        pergunta = dis.readUTF();
        ativa = dis.readBoolean();
        palavrasChave = dis.readUTF();
    }

    @Override
    public String toString() {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");
        String date = formatter.format(new Date(criacao));

        return date + "\n" + pergunta + "\n" + palavrasChave;
    }
}
