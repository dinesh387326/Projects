/*Code for non-linear regression*/
#include<iostream>
#include<fstream>
#include<cmath>
using namespace std;

int main(){
    ofstream wf("Output.txt");
    ifstream rf("Input.txt");

    int n;
    rf>>n;

    double e1;
    double e2;
    rf>>e1;
    rf>>e2;

    double a0;
    double a1;
    rf>>a0;
    rf>>a1;

    double *xi = new double [n];
    double *yi = new double [n];
    double *yin = new double [n];
    double *xin = new double [n];
    double *yp = new double [n];
    double *dev = new double [n];
    double **Z = new double*[n];

    for(int i=0;i<n;i++){
        Z[i] = new double[2];
    }

    double **Zt = new double*[2];

    for(int i=0;i<2;i++){
        Zt[i] = new double[n];
    }

    double *D = new double [n];

    for(int i = 0;i<n;i++){
        rf>>xi[i];
    }
    for(int i = 0;i<n;i++){
        rf>>yi[i];
    }
    for(int i= 0 ; i<n;i++){
        xin[i] = xi[i]/100.0;
    }
    for(int i= 0 ; i<n;i++){
        yin[i] = 1.0/yi[i];
    }

    bool flag = true;
    int counter = 0;
    double ZZt[2][2] = {{0,0},{0,0}};
    double ZtD[2] = {0,0};
    double arr_inv[2][2];
    double res[2] = {0,0};
    int ni = 2;
    double tol = 10e-6;

    while(flag){

        for(int i = 0 ; i<n ; i++){
            for (int j = 0 ; j<2 ; j++){
                if(j == 0){
                    Z[i][j] = pow(xin[i],e1);
                }
                else{
                    Z[i][j] = pow((1-xin[i]),e2);
                }
            }
        }

        for(int i = 0 ; i<n ; i++){
            D[i] = yin[i] - ((a0*pow(xin[i],e1)) + (a1*pow(1-xin[i],e2)) );
        }

        for(int i = 0 ; i<2 ; i++){
            for (int j = 0 ; j<n ; j++){
                    Zt[i][j] = Z[j][i];
            }
        }

        for(int i = 0 ; i<2 ; i++){
            for(int j = 0 ; j<2 ; j++){
                ZZt[i][j] = 0;
            }
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < n ; k++) {
                    ZZt[i][j] += Zt[i][k] * Z[k][j];
                }
            }
        }

        for(int i = 0 ; i<2 ; i++){
                ZtD[i] = 0;
        }
        
        for (int i = 0; i < 2; i++) {
                for (int k = 0; k < n ; k++) {
                    ZtD[i] += Zt[i][k] * D[k];
                }
        }

        for(int i = 0; i<2 ;i++){
            for(int j = 0;j<2;j++){
                if(i==j){
                    arr_inv[i][j] =1;
                }
                else{
                    arr_inv[i][j] = 0;
                }
            }
        }

        int k = 0;

        while(k<ni){

            int pr = k;
            double pe = ZZt[k][k];

            for(int i = k+1;i<ni;i++){
                if(ZZt[i][k]>pe){
                    pe = ZZt[i][k];
                    pr = i;
                }
            }

            double dummy;

            if(pr != k){
                for(int j = k;j<ni;j++){
                    dummy = ZZt[pr][j];
                    ZZt[pr][j] = ZZt[k][j];
                    ZZt[k][j] = dummy;
                }
                for(int j = 0;j<ni;j++){
                    dummy = arr_inv[pr][j];
                    arr_inv[pr][j] = arr_inv[k][j];
                    arr_inv[k][j] = dummy;
                }
            }
            
            double k1 = ZZt[k][k];

            for(int j = k;j<ni;j++){
                ZZt[k][j] = ZZt[k][j]/k1;
            }
            for(int j = 0 ; j<ni ;j++){
                arr_inv[k][j] = arr_inv[k][j]/k1;
            }

            for (int i = k+1;i<ni;i++){
                double k2 = ZZt[i][k];
                for(int j = k;j<ni;j++){
                    ZZt[i][j] = ZZt[i][j] - (k2) * (ZZt[k][j]);
                }
                for(int j = 0;j<ni;j++){
                    arr_inv[i][j] = arr_inv[i][j] - (k2) * (arr_inv[k][j]);
                }
            }

            for (int i=k-1;i>=0;i--){
                double k2 = ZZt[i][k];
                for(int j =k;j<ni;j++){
                    ZZt[i][j] = ZZt[i][j] - (k2) * (ZZt[k][j]);
                }
                for(int j =0;j<ni;j++){
                    arr_inv[i][j] = arr_inv[i][j] - (k2) * (arr_inv[k][j]);
                }
            }

            k+=1;
        }

        for(int i = 0 ; i<2 ; i++){
                res[i] = 0;
        }

        for (int i = 0; i < 2; i++) {
                for (int k = 0; k < 2 ; k++) {
                    res[i] += arr_inv[i][k]*ZtD[k];
                }
        }

        a0 = a0 + res[0];
        a1 = a1 + res[1];

        if(max(abs(res[0])/a0,abs(res[1])/a1)<tol){
            flag=false;
        }
    }

    for(int i = 0 ; i<9; i++){
        yp[i] = 1.0/((a0*pow(xin[i],e1)) + (a1*pow(1-xin[i],e2)));
    }

    wf<<"y predicted: ";
    wf<<endl;
    for(int i = 0 ; i<n; i++){
        wf<<yp[i]<<", ";
    }

    double ULFL1 = 1.0/(a0);
    double ULFL2 = 1.0/(a1);

    wf<<endl<<"a0 :"<<a0;
    wf<<endl<<"a1 :"<<a1;
    wf<<endl<<"UFL of 1 :"<<ULFL1;
    wf<<endl<<"UFL of 2 :"<<ULFL2;

    wf.close();
    rf.close();

    return 0;
}