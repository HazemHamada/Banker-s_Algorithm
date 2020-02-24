package banker.s_algorithm;


import java.util.Random;
import java.util.Scanner;


public class BankerS_Algorithm {
    //static Random rand;
    static final double MAXLIMIT = 0.9;
    static final double LOWERLIMIT = 0.4;
    static final double REQLIMIT = 0.6;
    static int n,m;
    static int []available;
    static int [][]max;
    static int [][]allocation;
    static int [][]need;
    static int counter = 1;
    
    public static void main(String[] args) {
        input();
        reqloop();    
    }
    
    public static void input(){
        
        Scanner sc = new Scanner(System.in);
        
        do{
            System.out.println("enter number of processes:");
            n = sc.nextInt();//processes
        }while(n <= 0);
        
        
        
        do{
            System.out.println("enter number of resources:");
            m = sc.nextInt();//processes
        }while(m <= 0);
        
        available = new int[m];
        max = new int[n][m];
        allocation = new int[n][m]; //intilized b zero automatically
        need = new int[n][m];
        
        System.out.println("enter instances of resources");
        for(int i = 0; i < m; i++){
            do{
            available[i] = sc.nextInt();
        }while(available[i] < 0);
        }
        
        //generating upper limit & lowerlimit for max
        int []upperlimit = new int[m];
        int []lowerlimit = new int[m];
        for (int i = 0; i < m; i++) {
            upperlimit[i] = (int)Math.ceil(available[i] * MAXLIMIT);//upper limit is MAXLIMIT% of available
            lowerlimit[i] = (int)Math.ceil(available[i] * LOWERLIMIT);//same here
        }
        
        Random randomGenerator = new Random();
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                max[i][j]= randomGenerator.nextInt((upperlimit[j] - lowerlimit[j]) + 1) + lowerlimit[j];//fokkak msh htfhm :D
            }
        }
        //need is the same as max in the beginning as allocation is zero
        for(int i = 0; i < n; i++)
            need[i] = max[i].clone();
        
        System.out.println("generating matrices ...");
        printmats();
    }
    
    
    static boolean safe(){
        //array to do avaliable caluclation in
        int []work = available.clone();
        
        boolean[] finish = new boolean[n];
        for(int i = 0; i < n; i++)
            finish[i] = false;
        
        boolean foundprocess = false;
        for(int i=0; i < n; i++){
            
            if (finish[i] == false){ // two check finish = false && need < work
                boolean need_less_available = true;
                for(int j = 0; j < m; j++){
                    if(!(need[i][j] <= work[j])) {
                        need_less_available = false;
                        break;
                    }
                }
                //if checks are met finish process
                if(need_less_available) {
                    for (int j = 0; j < m; j++) {
                        work[j] = work[j] + allocation[i][j];
                    }    
                    
                    finish[i] = true;
                    foundprocess = true;
                    
                }
            }
            //looping again if a process finished in the current loop
            if(i == n-1 && foundprocess) {
                i = -1;
                foundprocess = false;
            }
        }
        //check if all procees finished
        boolean alltrue = true;
        for(int i = 0; i < n; i++){
            if(finish[i]==false){
                alltrue = false;
                break;
            }
        }
       
        return alltrue;
    }
    
    static void resource_request(int[] request, int pnum){
        
        for(int i = 0; i < m; i++){
            if(request[i] > need[pnum][i]){
                System.out.println("error:the process has exceeded its maximum claim.");
                return;
            }
        }
        for(int i = 0; i < m; i++){
            if(request[i] > available[i]){
                System.out.println("request > available process i have to wait.");
                return;//or wait lw 2l doctor hyd5al 2l input we 2l brnameg berun
                
            }
        }
        request(request, pnum);//calc available, need etc ...
        printmats();
        System.out.println("checking if request is safe ...");
        
        if(!safe()) {
            request_inv(request, pnum);
            System.out.println("not safe reversing state");
            printmats();
        }
        else {
            System.out.println("safe");
        }
    }
    
    static void request(int[]request, int pnum){        
        
        for(int i = 0; i < m; i++){
            available[i] -= request[i];
            allocation[pnum][i] += request[i];
            need[pnum][i] -= request[i];
        }   
    }
    
    static void request_inv(int[] request, int pnum){        
        
        for(int i = 0; i < m; i++){
            available[i] += request[i];
            allocation[pnum][i] -= request[i];
            need[pnum][i] += request[i];
        }
        
    }
    
    static void printmats() {
        System.out.println(String.format("%-40s%-40s%-40s%-40s", "allocation", "max", "need", "available"));
        //dah bykrar el "-" 160 mara
        String repeated = new String(new char[160]).replace("\0", "-");
        System.out.println(repeated);
        
        for (int i = 0; i < n; i++) {
            String alloc_string = "";
            String max_string = "";
            String need_string = "";
            String available_string = "";
            
            for (int j = 0; j < m; j++) {
                alloc_string = alloc_string + String.format("%-2d ", allocation[i][j]);
                max_string = max_string + String.format("%-2d ", max[i][j]);
                need_string = need_string + String.format("%-2d ", need[i][j]);
                if(i==0)
                    available_string = available_string + String.format("%-2d ", available[j]);
            }
            
            System.out.println(String.format("%-40s%-40s%-40s%-40s", alloc_string, max_string, need_string, available_string));
        }
    }
    
    static void reqloop() {
        Scanner sc = new Scanner(System.in);
        boolean flag = true;
        while(flag){
            System.out.println("'r' to request 'e' to exit resources are randomly released every 2 requests");
            switch(sc.next().charAt(0)){
                case 'r':
                    reqgen();
                    if(counter % 2 == 0)
                        randomrelease();
                    counter++;
                    break;
                case 'e':
                    flag = false;
                    break;
                default:
                    System.out.println("invalid enter 'r' to request 'e' to exit");   
            }
        }        
    }
    
    static void reqgen() {
        Random randomGenerator = new Random();
        int pnum;
        int request[] = new int [m];
        
        if(zeromatrix(need)){
            System.out.println("need matrix is zero can't request any more resources");
            return;
        }
        
        while(true){
            pnum = randomGenerator.nextInt(n);
            int upperlimit ;
            for (int i = 0; i < m; i++) {
                //do{
                 upperlimit = (int)Math.ceil(need[pnum][i] * REQLIMIT);
               
                //}while(upperlimit>available[i]);
                request[i] = randomGenerator.nextInt(upperlimit + 1);
            }
            if(!zeroarray(request)) {
                break;
            }
        }
        
        
        System.out.print("p" + pnum+" requesting ");
        for (int i = 0; i < request.length; i++) {
            System.out.print(request[i]+ " ");
        }
        System.out.println();
        resource_request(request, pnum);   
    }
    
    static boolean zeroarray(int a[]){
        for (int i = 0; i <a.length; i++) {
            if(a[i] != 0)
                return false;   
        }
        return true;
    }
    static void randomrelease() {
        Random randomGenerator = new Random();
        int pnum;
        int release[] = new int [m];
        
        if(zeromatrix(allocation)){
            System.out.println("allocation matrix is zero can't release any more resources exiting ...");
            System.exit(0);
        }
        
        while(true){
            pnum = randomGenerator.nextInt(n);
            for (int i = 0; i < m; i++) {
                release[i] = randomGenerator.nextInt(allocation[pnum][i] + 1);
            }
            if(!zeroarray(release)) {
                break;
            }
        }
        
        for (int i = 0; i < m; i++) {
            allocation[pnum][i] -= release[i];
            available[i] += release[i];
        }
        
        System.out.print("p" + pnum+" releasing ");
        for (int i = 0; i < release.length; i++) {
            System.out.print(release[i]+ " ");
        }
        System.out.println();
        printmats();
        
    }
    
    static boolean zeromatrix(int a[][]){
        for (int i = 0; i <n; i++) {
            for (int j = 0; j < m; j++) {
                if(a[i][j] != 0)
                    return false; 
            }    
        }
        return true;
    }
    
}

    
    









/*import java.util.Random;
import java.util.Scanner;


public class BankerS_Algorithm {
    //static Random rand;
    static final double MAXLIMIT = 0.7;
    static final double LOWERLIMIT = 0.2;
    static final double REQLIMIT = 0.6;
    static int n,m;
    static int []available;
    static int [][]max;
    static int [][]allocation;
    static int [][]need;
    
    public static void main(String[] args) {
        input();
        reqloop();
    }
    
    public static void input(){
        
        Scanner sc = new Scanner(System.in);
        System.out.println("enter number of processes:");
        n = sc.nextInt();//processes
        
        System.out.println("enter number of resources:");
        m = sc.nextInt();//resources
        
        available = new int[m];
        max = new int[n][m];
        allocation = new int[n][m]; //intilized b zero automatically
        need = new int[n][m];
        
        System.out.println("enter instances of resources");
        for(int i = 0; i < m; i++){
            available[i] = sc.nextInt();
        }
        
        //generating upper limit & lowerlimit for max
        int []upperlimit = new int[m];
        int []lowerlimit = new int[m];
        for (int i = 0; i < m; i++) {
            upperlimit[i] = (int)Math.ceil(available[i] * MAXLIMIT);//upper limit is MAXLIMIT% of available
            lowerlimit[i] = (int)Math.ceil(available[i] * LOWERLIMIT);//same here
        }
        
        Random randomGenerator = new Random();
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                max[i][j]= randomGenerator.nextInt((upperlimit[j] - lowerlimit[j]) + 1) + lowerlimit[j];//fokkak msh htfhm :D
            }
        }
        //need is the same as max in the beginning as allocation is zero
        for(int i = 0; i < n; i++)
            need[i] = max[i].clone();
        
        System.out.println("generating matrices ...");
        printmats();
    }
    
    
     static boolean safe(){
        //array to do avaliable caluclation in
        int []work = available.clone();
        
        boolean[] finish = new boolean[n];
        for(int i = 0; i < n; i++)
            finish[i] = false;
        
        boolean foundprocess = false;
        for(int i=0; i < n; i++){
            
            if (finish[i] == false){ // two check finish = false && need < work
                boolean need_less_available = true;
                for(int j = 0; j < m; j++){
                    if(!(need[i][j] <= work[j])) {
                        need_less_available = false;
                        break;
                    }
                }
                //if checks are met finish process
                if(need_less_available) {
                    for (int j = 0; j < m; j++) {
                        work[j] = work[j] + allocation[i][j];
                    }    
                    
                    finish[i] = true;
                    foundprocess = true;
                    
                }
            }
            //looping again if a process finished in the current loop
            if(i == n-1 && foundprocess) {
                i = -1;
                foundprocess = false;
            }
        }
        //check if all procees finished
        boolean alltrue = true;
        for(int i = 0; i < n; i++){
            if(finish[i]==false){
                alltrue = false;
                break;
            }
        }
       
        return alltrue;
    }
    
    static void resource_request(int[] request, int pnum){
        
        for(int i = 0; i < m; i++){
            if(request[i] > need[pnum][i]){
                System.out.println("error:the process has exceeded its maximum claim.");
                return;
            }
        }
        for(int i = 0; i < m; i++){
            if(request[i] > available[i]){
                System.out.println("request > available process i have to wait.");
                return;//or wait lw 2l doctor hyd5al 2l input we 2l brnameg berun
                
            }
        }
        request(request, pnum);//calc available, need etc ...
        printmats();
        System.out.println("checking if request is safe ...");
        
        if(!safe()) {
            request_inv(request, pnum);
            System.out.println("not safe reversing state");
            printmats();
            System.out.println("'r' to request 'e' to exit");
        }
        else {
            System.out.println("safe");
            System.out.println("'r' to request 'e' to exit");
        }
    }
    
    static void request(int[]request, int pnum){        
        
        for(int i = 0; i < m; i++){
            available[i] -= request[i];
            allocation[pnum][i] += request[i];
            need[pnum][i] -= request[i];
        }   
    }
    
    static void request_inv(int[] request, int pnum){        
        
        for(int i = 0; i < m; i++){
            available[i] += request[i];
            allocation[pnum][i] -= request[i];
            need[pnum][i] += request[i];
        }
        
    }
    
    static void printmats() {
        System.out.println(String.format("%-40s%-40s%-40s%-40s", "allocation", "max", "need", "available"));
        //dah bykrar el "-" 160 mara
        String repeated = new String(new char[160]).replace("\0", "-");
        System.out.println(repeated);
        
        for (int i = 0; i < n; i++) {
            String alloc_string = "";
            String max_string = "";
            String need_string = "";
            String available_string = "";
            
            for (int j = 0; j < m; j++) {
                alloc_string = alloc_string + String.format("%-2d ", allocation[i][j]);
                max_string = max_string + String.format("%-2d ", max[i][j]);
                need_string = need_string + String.format("%-2d ", need[i][j]);
                if(i==0)
                    available_string = available_string + String.format("%-2d ", available[j]);
            }
            
            System.out.println(String.format("%-40s%-40s%-40s%-40s", alloc_string, max_string, need_string, available_string));
        }
    }
    
    static void reqloop() {
        System.out.println("'r' to request 'e' to exit");
        Scanner sc = new Scanner(System.in);
        boolean flag = true;
        while(flag){
            switch(sc.nextLine().charAt(0)){
                case 'r':
                    reqgen();
                    break;
                case 'e':
                    flag = false;
                    break;
                default:
                    System.out.println("invalid enter 'r' to request 'e' to exit");   
            }
        }        
    }
    
    static void reqgen() {
        Random randomGenerator = new Random();
        int pnum = randomGenerator.nextInt(n);
        int request[] = new int [m];
        for (int i = 0; i < m; i++) {
            int upperlimit = (int)Math.ceil(need[pnum][i]);
            request[i] = randomGenerator.nextInt(upperlimit + 1);
        }
        
        System.out.print("p" + pnum+" ");
        for (int i = 0; i < request.length; i++) {
            System.out.print(request[i]+ " ");
        }
        System.out.println();
        resource_request(request, pnum);   
    }
}
    */
    






/*import static java.lang.Double.min;
import java.util.Random;
import java.util.Scanner;

//2na 2l 2rkam 2l random 7tetha constants for testing bs
public class BankerS_Algorithm {
    static Random rand;
    static int n,m;
    public static void main(String[] args) {
        
        
        int []available;
        int [][]max;
        int [][]allocation;
        int [][]need;
        
        Scanner sc =new Scanner(System.in);
        System.out.println("enter number of processes:");
        n=sc.nextInt();//processes
        System.out.println("enter number of resources:");
        m=sc.nextInt();//resources
        available=new int[m];
        max=new int[n][m];
        allocation=new int[n][m];
        need=new int[n][m];
        //System.out.println("please Enter the allocation matics");
        //fillArr(allocation);
        for(int i=0;i<m;i++){
            System.out.println("Enter num of instances of resource "+i);
            int g= sc.nextInt();
            available[i]=g;
        }
        int g;
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                //g = rand.nextInt(available[j]);
                //max[i][j]= g;
                //need[i][j]=g;
                //max[i][j]=10;
                //need[i][j]=10;
            }
        }
        
        for(int i=0; i<n;i++){
            //g=rand.nextInt(n)+1;
            resource_request(m, max, n, 1, need, available, allocation);
        }
        
        
        
        
    }
    
    static boolean safe(int[]available,int[][] max, int[][]allocation, int[][]need,int n, int m ){
        
        int [] work=new int[m];
        System.arraycopy(available, 0, work, 0, m);
        boolean b=true;
        boolean a=true;
        boolean[] finish=new boolean[n];
        for(int i=0;i<n;i++)
            finish[i]=false;
        for(int i=0;i<n;i++){
            if(finish[i]=false){
                for(int j=0;j<m;j++){
                    if(need[i][j]>work[j])
                        a=false;
                }
            }
            if(a==true){
                for(int j=0;j<m;j++){
                    work[j]+=allocation[i][j];
                    finish[i]=true;
                }
            }
        }
        for(int i=0;i<n;i++){
            if(finish[i]==false){
                b=false;
                break;
            }
        }
       
        return b;
    }
    
    static void request(int m,int[]available, int[]requesti, int[]needi, int[]allocationi){        
        
        for(int i=0;i<m;i++){
            available[i]-=requesti[i];
            allocationi[i]+=requesti[i];
            needi[i]-=requesti[i];
        }
        
    }
    
    static void request_inv(int m,int[]available, int[]requesti, int[]needi, int[]allocationi){        
        
        for(int i=0;i<m;i++){
            available[i]+=requesti[i];
            allocationi[i]-=requesti[i];
            needi[i]+=requesti[i];
        }
        
    }
    
    static void resource_request(int m,int[][] max,int n, int r, int[][]need, int[]available,/* int[]request, int[][]allocation){
        
        int []request=new int[m];
        for(int i=0;i<m;i++){
            request[i]=1;//rand.nextInt(need[r][i]);
        }
        
        for(int i=0;i<m;i++){
            if(request[i]>need[r][i]){
                System.out.println("error:the process has exceeded its maximum claim.");
                return;
            }
        }
        for(int i=0;i<m;i++){
            if(request[i]>available[i]){
                System.out.println("process i have to wait.");
                return;//or wait lw 2l doctor hyd5al 2l input we 2l brnameg berun
            }
        }
        request(m,available, request, need[r], allocation[r]);
        if(!(safe(available, max, allocation, need, n, m ))){
            request_inv(m,available, request, need[r], allocation[r]);
                            

        }
    }
 
    static void fillArr(int[][] a){
    Scanner sc =new Scanner(System.in);
    for(int j=0;j<n;j++){    
        for(int i=0;i<m;i++){
            a[j][i]=sc.nextInt();
        }
    }
    }
    
    
}*/


/*public class BankerS_Algorithm {

    public static void main(String[] args) {
        
        int n,m;
        int []available;
        int [][]max;
        int [][]allocation;
        int [][]need;
        
        Scanner sc =new Scanner(System.in);
        n=sc.nextInt();//processes
        m=sc.nextInt();//resources
        available=new int[m];
        max=new int[n][m];
        allocation=new int[n][m];
        need=new int[n][m];
        
        
    
    }
    
    boolean safe(int[]available,int[][] max, int[][]allocation, int[][]need,int n, int m ){
        
        boolean b=true;
        boolean a=true;
        boolean[] finish=new boolean[n];
        for(int i=0;i<n;i++)
            finish[i]=false;
        for(int i=0;i<n;i++){
            if(finish[i]=false){
                for(int j=0;j<m;j++){
                    if(need[i][j]>available[j])
                        a=false;
                }
            }
            if(a==true){
                for(int j=0;j<m;j++){
                    available[j]+=allocation[i][j];
                    finish[i]=true;
                }
            }
        }
        for(int i=0;i<n;i++){
            if(finish[i]==false){
                b=false;
                break;
            }
        }
       
        return b;
    }
    
    void request(int m,int[]available, int[]requesti, int[]needi, int[]allocationi){        
        
        for(int i=0;i<m;i++){
            available[i]-=requesti[i];
            allocationi[i]+=requesti[i];
            needi[i]-=requesti[i];
        }
        
    }
    
    void request_inv(int m,int[]available, int[]requesti, int[]needi, int[]allocationi){        
        
        for(int i=0;i<m;i++){
            available[i]+=requesti[i];
            allocationi[i]-=requesti[i];
            needi[i]+=requesti[i];
        }
        
    }
    
    void resource_request(int m,int[][] max,int n, int r, int[][]need, int[]available, int[][]request, int[][]allocation){
        
        for(int i=0;i<m;i++){
            if(request[r][i]>need[r][i]){
                System.out.println("error:the process has exceeded its maximum claim.");
                return;
            }
        }
        for(int i=0;i<m;i++){
            if(request[r][i]>available[i]){
                System.out.println("process i have to wait.");
                return;//or wait lw 2l doctor hyd5al 2l input we 2l brnameg berun
                
            }
        }
        request(m,available, request[r], need[r], allocation[r]);
        if(!(safe(available, max, allocation, need, n, m ))){
            request_inv(m,available, request[r], need[r], allocation[r]);
        }
    }
     
}
*/