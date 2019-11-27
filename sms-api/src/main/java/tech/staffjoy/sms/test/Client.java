package tech.staffjoy.sms.test;

import java.util.ArrayList;
import java.util.List;

/**
 *将对象组合成树形结构以表示 “部分-整体” 的层次结构，使得用户对单个对象和组合对象的使用具有一致性
 * 
 * @author MABEIXUE
 * @date 2019-11-27
 *
 */

public class Client {
    public static void main(String[] args) {
        //创建根节点及其子节点
        Composite root = new Composite("root");
        root.add(new Leaf("Leaf A"));
        root.add(new Leaf("Leaf B"));

        //创建第二层节点及其子节点
        Composite branch = new Composite("Composite X");
        branch.add(new Leaf("Leaf XA"));
        branch.add(new Leaf("Leaf XB"));
        root.add(branch);
        
        //创建第三层节点及其子节点
        Composite branch2 = new Composite("Composite XY");
        branch2.add(new Leaf("Leaf XYA"));
        branch2.add(new Leaf("Leaf XYB"));
        branch.add(branch2);
        
        //创建第二层节点
        root.add(new Leaf("Leaf C"));
        
        //创建第二层节点并删除
        Leaf leaf = new Leaf("Leaf D");
        root.add(leaf);
        root.remove(leaf);
        
        //打印
        root.display(1);
    }
    
}

 abstract class Component {
    
    protected String name;
    
    public Component(String name) {
        this.name = name;
    }

    //增加一个叶子构件或树枝构件
    public abstract void add(Component component);
    
    //删除一个叶子构件或树枝构件
    public abstract void remove(Component component);
    
    //获取分支下的所有叶子构件和树枝构件
    public abstract void display(int depth);
    
}
 
  class Composite extends Component {

     public Composite(String name) {
         super(name);
     }

     //构建容器
     private ArrayList<Component> componentArrayList = new ArrayList<Component>();
     
     @Override
     public void add(Component component) {
         this.componentArrayList.add(component);
     }

     @Override
     public void remove(Component component) {
         this.componentArrayList.remove(component);
     }

     @Override
     public void display(int depth) {
         //输出树形结构
         for(int i=0; i<depth; i++) {
             System.out.print('-');
         }
         System.out.println(name);
         
         //下级遍历
         for (Component component : componentArrayList) {
             component.display(depth + 1);
         }
     }

 }
  
   class Leaf extends Component {

      public Leaf(String name) {
          super(name);
      }

      @Override
      public void add(Component component) {
          //空实现，抛出“不支持请求”异常
          throw new UnsupportedOperationException();
      }

      @Override
      public void remove(Component component) {
          //空实现，抛出“不支持请求”异常
          throw new UnsupportedOperationException();
      }

      @Override
      public void display(int depth) {
          //输出树形结构的叶子节点
          for(int i=0; i<depth; i++) {
              System.out.print('-');
          }
          System.out.println(name);
      }

  }
