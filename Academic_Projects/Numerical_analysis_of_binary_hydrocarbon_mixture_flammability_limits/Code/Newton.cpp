/*Code for Newton's Divided Difference Method*/
#include<iostream>
#include<cmath>
#include<fstream>
using namespace std;

double *c;
double *xm;

double finalf(double x){

    double fx;

    fx = c[0] + c[1]*(x - xm[0]) + c[2]*(x - xm[0])*(x - xm[1]) + c[3]*(x - xm[0])*(x - xm[1])*(x- xm[2])
    + c[4]*(x - xm[0])*(x - xm[1])*(x- xm[2])*(x - xm[3]);

    return fx;
}

int main(){
    ofstream wf("Output.txt");
    ifstream rf("Input.txt");

    int n = 5;
    int m;

    double *xi = new double [n];
    double *yi = new double [n];

    for(int i = 0;i<n;i++){
        rf>>xi[i];
    }
    for(int i = 0;i<n;i++){
        rf>>yi[i];
    }

    rf>>m;

    double *yip = new double [m];
    double *xir = new double [m];

    for(int i = 0;i<m;i++){
        rf>>xir[i];
    }

    double f1[4];
    double f2[3];
    double f3[2];
    double f4[1];

    double b[5];

    for(int i = 0 ; i<4 ; i++){
        f1[i] = (yi[i+1] - yi[i])/(xi[i+1]-xi[i]);
    }

    for(int i = 0 ; i<3 ; i++){
        f2[i] = (f1[i+1] - f1[i])/(xi[i+2]-xi[i]);
    }
    for(int i = 0 ; i<2 ; i++){
        f3[i] = (f2[i+1] - f2[i])/(xi[i+3]-xi[i]);
    }
    for(int i = 0 ; i<1 ; i++){
        f4[i] = (f3[i+1] - f3[i])/(xi[i+4]-xi[i]);
    }

    b[0] = yi[0];
    b[1] = f1[0];
    b[2] = f2[0];
    b[3] = f3[0];
    b[4] = f4[0];

    c = b;
    xm = xi;

    for(int i = 0; i<9; i++){
        yip[i] = finalf(xir[i]);
    }

    wf<<"y predicted: "<<endl;
    for(int i = 0; i<9; i++){
        wf<< yip[i]<<" ,";
    }
    wf<<endl;
    for(int i = 0; i<5; i++){
        wf<<"b"<<i<<" "<<b[i]<<endl;
    }

    wf.close();
    rf.close();

    return 0;
}