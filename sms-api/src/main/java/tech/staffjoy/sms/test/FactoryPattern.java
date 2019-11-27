package tech.staffjoy.sms.test;


/**
 * 工厂模式（Factory Pattern）是 Java 中最常用的设计模式之一。这种类型的设计模式属于创建型模式，它提供了一种创建对象的最佳方式。
   在工厂模式中，我们在创建对象时不会对客户端暴露创建逻辑，并且是通过使用一个共同的接口来指向新创建的对象。
 * TODO
 * @author MABEIXUE
 * @date 2019-11-14
 *
 *
 *
 * 建造者模式最主要功能是基本方法的调用顺序安排，
 * 也就是这些基本方法已经实现了；
 * 而工厂方法则重点是创建，你要什么对象我创造一
 * 个对象出来，组装顺序则不是他关心的。
 */
public class FactoryPattern {
    public static void main(String[] args) {
        ShapeFactory shapeFactory = new ShapeFactory();
        
        //获取 Circle 的对象，并调用它的 draw 方法
        Shape shape1 = shapeFactory.getShape("CIRCLE");
   
        //调用 Circle 的 draw 方法
        shape1.draw();
   
        //获取 Rectangle 的对象，并调用它的 draw 方法
        Shape shape2 = shapeFactory.getShape("RECTANGLE");
   
        //调用 Rectangle 的 draw 方法
        shape2.draw();
   
        //获取 Square 的对象，并调用它的 draw 方法
        Shape shape3 = shapeFactory.getShape("SQUARE");
   
        //调用 Square 的 draw 方法
        shape3.draw();
    }
    
}

 interface Shape {
    void draw();
 } 
class Rectangle implements Shape {
    
    @Override
    public void draw() {
       System.out.println("Inside Rectangle::draw() method.");
    }
 }

 class Square implements Shape {
    
    @Override
    public void draw() {
       System.out.println("Inside Square::draw() method.");
    }
 }

 class Circle implements Shape {
    @Override
    public void draw() {
       System.out.println("Inside Circle::draw() method.");
    }
 }
 class ShapeFactory {
    
    //使用 getShape 方法获取形状类型的对象
    public Shape getShape(String shapeType){
       if(shapeType == null){
          return null;
       }        
       if(shapeType.equalsIgnoreCase("CIRCLE")){
          return new Circle();
       } else if(shapeType.equalsIgnoreCase("RECTANGLE")){
          return new Rectangle();
       } else if(shapeType.equalsIgnoreCase("SQUARE")){
          return new Square();
       }
       return null;
    }
 }

