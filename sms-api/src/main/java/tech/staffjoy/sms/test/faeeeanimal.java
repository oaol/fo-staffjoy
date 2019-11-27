package tech.staffjoy.sms.test;

public class faeeeanimal {
    public static void main(String[] args) {
        AnimalFactory factory = new CatFactory();
        Animal animal = factory.create();
        animal.say();
    }
}

 class Dog implements Animal{
    @Override
    public void say() {
        // TODO Auto-generated method stub
        System.out.println("狗狗叫: 旺旺....");
    }
}
  class Cat implements Animal {
     @Override
     public void say() {
         // TODO Auto-generated method stub
         System.out.println("猫猫叫： 喵喵....");  
     }
  }
 interface Animal {
    public void say() ;
}
     

 class CatFactory implements AnimalFactory {
    public Animal create() {
        // TODO Auto-generated method stub
        return new Cat();
    }
}   
 

 class DogFactory implements AnimalFactory {
    @Override
    public Animal create() {
        // TODO Auto-generated method stub
        return new Dog();
    }
 }
  interface AnimalFactory {
     //这个是创造对象的动作规则;
     public Animal create();
}