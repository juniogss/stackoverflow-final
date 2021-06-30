package com.perguntas.models;

import com.perguntas.interfaces.Register;

import java.io.*;

public class Vote implements Register {

    private int idVote;
    private int idUser;
    private String type;
    private int idReference;
    private boolean vote;

    public Vote() {
        this.idVote = -1;
        this.idUser = -1;
        this.type = "";
        this.idReference = -1;
        this.vote = false;
    }

    public Vote(int idUser, String type, int idReference, boolean vote) {
        this.idVote = -1;
        this.idUser = idUser;
        this.type = type;
        this.idReference = idReference;
        this.vote = vote;
    }

    @Override
    public int getID() {
        return idVote;
    }

    @Override
    public void setID(int n) {
        this.idVote = n;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIdReference() {
        return idReference;
    }

    public void setIdReference(int idReference) {
        this.idReference = idReference;
    }

    public boolean isVote() {
        return vote;
    }

    public void setVote(boolean vote) {
        this.vote = vote;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idVote);
        dos.writeUTF(type);
        dos.writeInt(idUser);
        dos.writeInt(idReference);
        dos.writeBoolean(vote);
        dos.close();
        baos.close();

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idVote = dis.readInt();
        type = dis.readUTF();
        idUser = dis.readInt();
        idReference = dis.readInt();
        vote = dis.readBoolean();
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + idVote +
                ", idUser=" + idUser +
                ", type=" + type +
                ", idReference=" + idReference +
                ", vote=" + vote +
                '}';
    }
}
