import java.io.*;
import java.util.*;
class negativeException extends Exception{
    public negativeException(String str){
        super(str);
    }
}
class Product{
    String pId;
    String pName;
    double pPrice;
    int pQuantity;
    Product(String pId,String pName,double pPrice,int pQuantity){
        this.pId=pId;
        this.pName=pName;
        this.pPrice=pPrice;
        this.pQuantity=pQuantity;
    }
}
class Inventory{
    static HashMap<String,Product> map=new HashMap<String, Product>();

    static void readProducts(){
        try {
            File myObj = new File("src/Products.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] productDetails=data.split(",");
                String productId=productDetails[0];
                String productName=productDetails[1];
                double productPrice=Double.parseDouble(productDetails[2]);
                int productQuantity=Integer.parseInt(productDetails[3]);
                Product temp=new Product(productId,productName,productPrice,productQuantity);
                Inventory.addProduct_in_map(temp);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
    static void addNewProduct() throws negativeException{
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter id of Product : ");
        String id=sc.next();
        System.out.println("Enter Name of Product : ");
        String name=sc.next();
        System.out.println("Enter Product Price : ");
        double price=sc.nextDouble();
        if(price<0){
            throw new negativeException("No Negative Values allowed");
        }
        System.out.println("Enter Product Quantity : ");
        int quant=sc.nextInt();
        if(quant<0){
            throw new negativeException("No Negative Values allowed");
        }
        Product temp=new Product(id,name,price,quant);
        Inventory.addProduct_in_map(temp);
        String product=id+","+name+","+price+","+quant;
        try {
            FileWriter myWriter = new FileWriter("src/Products.txt",true);
            BufferedWriter bf=new BufferedWriter(myWriter);
            bf.write(product);
            bf.newLine();
            bf.close();
        } catch (IOException e) {
            System.out.println("An error occurred in logging file");
            e.printStackTrace();
        }


    }

    static void updateLog(String logInfo){
        try {
            FileWriter myWriter = new FileWriter("src/logs.txt",true);
            BufferedWriter bf=new BufferedWriter(myWriter);
            bf.write(logInfo);
            bf.newLine();
            bf.close();
        } catch (IOException e) {
            System.out.println("An error occurred in logging file");
            e.printStackTrace();
        }

    }
    static void updatePurchasesLog(String logInfo){
        try {
            FileWriter myWriter = new FileWriter("src/purchases.txt",true);
            BufferedWriter bf=new BufferedWriter(myWriter);
            bf.write(logInfo);
            bf.newLine();
            bf.close();
        } catch (IOException e) {
            System.out.println("An error occurred in logging purchases file");
            e.printStackTrace();
        }

    }

    static void addProduct_in_map(Product newProduct){
        if(map.get(newProduct.pId)!=null){
            Product curr=map.get(newProduct.pId);
            if(curr.pName.equals(newProduct.pName)){
                if(curr.pPrice<newProduct.pPrice){
                    curr.pPrice=newProduct.pPrice;
                }
                else if(curr.pQuantity<newProduct.pQuantity){
                    curr.pQuantity=newProduct.pQuantity;
                }
            }
            else{
                System.out.println("Already id is used with other item");
            }
            map.put(curr.pId,curr);
        }
        else{
            map.put(newProduct.pId,newProduct);

            Inventory.updateLog("Product read from file and added!!");
        }
    }
    static void getProductDetails(){
        System.out.println("P-ID\t"+"P-Name\t"+"P-Price\t"+"P-Quantity");
        for(Map.Entry<String,Product> entry:map.entrySet()){
            Product curr=entry.getValue();
            System.out.println(curr.pId+" "+curr.pName+" "+curr.pPrice+" "+curr.pQuantity);
        }
        Inventory.updateLog("Product Details Viewed");
    }
    static void getSpecificProduct(String id){
        Product curr=map.get(id);
        System.out.println("P-ID\t"+"P-Name\t"+"P-Price\t"+"P-Quantity");
        System.out.println(curr.pId+" "+curr.pName+" "+curr.pPrice+" "+curr.pQuantity);
        Inventory.updateLog("Specific Product Viewed : "+curr.pName);
    }
    static synchronized void purchase_product(String id,int quantity){
        Product currProduct=map.get(id);
        if(currProduct.pQuantity>quantity){
            //purchasing product
            try{
                Thread.sleep(1000);

            }catch (InterruptedException e){
                System.out.println("Exception : "+e);
            }
            //updating inventory
            currProduct.pQuantity=currProduct.pQuantity-quantity;
            map.put(currProduct.pId,currProduct);
            updatePurchasesLog("Purchased "+quantity+" "+currProduct.pName+" "+Thread.currentThread().getName());
        }
        else{
            System.out.println("Insufficient Stock for the Product");

        }

    }

    static void General_Report(){
        double sum=0;
        try {
            FileWriter myWriter = new FileWriter("src/generalReport.txt",true);
            BufferedWriter bf=new BufferedWriter(myWriter);
            for(Map.Entry<String,Product> entry:map.entrySet()){
                Product curr=entry.getValue();
                sum=sum+(curr.pPrice*curr.pQuantity);
                String product=curr.pId+","+curr.pName+","+curr.pPrice+","+curr.pQuantity;
                bf.write(product);
                bf.newLine();
            }
            bf.write("The Total Value of Inventory : "+sum);
            bf.newLine();
            bf.close();
        } catch (IOException e) {
            System.out.println("An error occurred in logging in General Report file");
            e.printStackTrace();
        }

        System.out.println("Total Value of Inventory is : "+sum);
        Inventory.updateLog("General Report is Updated ");
    }
    static void Individual_Report(String id){
        Product curr=map.get(id);
        String product=curr.pId+","+curr.pName+","+curr.pPrice+","+curr.pQuantity;
        try {
            FileWriter myWriter = new FileWriter("src/specificReport.txt",true);
            BufferedWriter bf=new BufferedWriter(myWriter);
            bf.write(product);
            bf.newLine();
            bf.write("Value is : "+curr.pPrice*curr.pQuantity);
            bf.close();
        } catch (IOException e) {
            System.out.println("An error occurred in logging file");
            e.printStackTrace();
        }
        Inventory.updateLog("Individual Product Report is Updated");


    }
    static boolean isEmpty(){
        return map.isEmpty();
    }

}

class PurchaseThread extends Thread{
    String productId;
    int quantity;
    PurchaseThread(String productId,int quantity){
        this.productId=productId;
        this.quantity=quantity;
    }
    public void run(){
        Inventory.purchase_product(productId,quantity);
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Welcome To Inventory!!");
        System.out.println("Products from files are retrieved!!");
        Inventory.readProducts();
        while(true){
            System.out.println("Enter Your Choice");
            System.out.println("1.Enter 1 to add Product\n"+"2.Enter 2 to purchase\n"+"3.Details about Specific Product\n"+"4.Report\n");
            int choice=sc.nextInt();
            switch (choice){
                case 1:addNewProduct();
                    break;
                case 2: purchaseProduct();
                    break;
                case 3:getSpecificProductDetail();
                    break;
                case 4:getReport();
                    break;
                default:
                    System.out.println("Enter Proper Option ");
            }
            System.out.println("\nPress any number to continue or Press 2 to exit!!  ");
            int userOption=sc.nextInt();
            System.out.println();
            if(userOption==2){
                break;
            }
        }

    }

    static void addNewProduct(){
        try{
            Inventory.addNewProduct();
        }catch(negativeException e){
            System.out.println(e);
        }
    }
    static void purchaseProduct(){
        Scanner sc=new Scanner(System.in);
        if(!Inventory.isEmpty()){
            Inventory.getProductDetails();
            System.out.println("Enter Product Id to purchase : ");
            String p_id=sc.next();
            System.out.println("Enter Product Quantity : ");
            int quantity=sc.nextInt();
            Thread t1=new PurchaseThread(p_id,quantity);
            //Thread t2=new PurchaseThread("P-ID-1",1);
            t1.start();
            //t2.start();
        }
        else{
            System.out.println("Inventory is Empty!!");
        }

    }
    static void getSpecificProductDetail(){
        Scanner sc=new Scanner(System.in);
        if(!Inventory.isEmpty()){
            System.out.println("Enter Product Id to get Details : ");
            String p_id=sc.next();
            Inventory.getSpecificProduct(p_id);
        }
        else{
            System.out.println("Inventory is Empty!!");
        }

    }
    static void getReport(){
        Scanner sc=new Scanner(System.in);
        if(!Inventory.isEmpty()){
            System.out.println("1.Enter 1 to get one Product Report\n"+"2.Enter 2 to get overall Report");
            int report_choice=sc.nextInt();
            switch(report_choice){
                case 1:
                    System.out.println("Enter Product ID : ");
                    String p_id=sc.next();
                    Inventory.getSpecificProduct(p_id);
                    Inventory.Individual_Report(p_id);
                    break;
                case 2:
                    Inventory.getProductDetails();
                    Inventory.General_Report();
                    break;
            }
        }
        else{
            System.out.println("Inventory is Empty!!");
        }

    }

}
