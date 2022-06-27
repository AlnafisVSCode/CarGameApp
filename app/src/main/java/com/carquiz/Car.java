package com.carquiz;

class Car {

    private String fileName;
    private String companyName;

    public String getFileName() {
        return fileName;
    }

    public Car setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Car setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    @Override
    public String toString() {
        return "Car{" +
                "fileName='" + fileName + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}
