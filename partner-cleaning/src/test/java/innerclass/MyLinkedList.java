package innerclass;

/**
 * 链表的头结点不存放任何元素  最后一个节点指向null
 */
public class MyLinkedList {


    public static void main(String[] args) {
        MyLinkedList myLinkedList = new MyLinkedList();

        myLinkedList.add(new LinkNode(1, "SKT"));
        myLinkedList.add(new LinkNode(2, "RNG"));
        myLinkedList.add(new LinkNode(3, "IG"));
        myLinkedList.add(new LinkNode(4, "FPX"));
        myLinkedList.add(new LinkNode(5, "G2"));

        myLinkedList.Reversal();
    }


    //创建一个头结点
    private LinkNode<String> headNode = new LinkNode<String>(0, "");

    /**
     * 将新的节点元素添加到这个链表的末尾处，那么就需要先遍历这个链表，如果末尾元素的next为null那么到达尾部
     * 就可以添加新的节点了
     *
     * @param linkNode
     */
    public void add(LinkNode linkNode) {

        //创建 一个临时节点存放 头元素和next节点   因为头结点不能变
        LinkNode temp = headNode;

        //遍历这个链表  判断有没有到达链表的尾部
        while (true) {
            if (temp.next == null) {
                break;
            }
            //如果不为空的话 那么将临时节点 改为这个节点的下一个元素
            temp = temp.next;
        }
        //此时temp为这个链表的尾节点  添加节点的时候  直接将新的节点放到这个节点的next上面去即可
        temp.next = linkNode;
    }


    /**
     * 排序插入
     *
     * @param linkNode
     */
    public void sortAdd(LinkNode linkNode) {

        //创建一个临时节点  暂时让头结点等于这个临时节点
        LinkNode temp = headNode;

        boolean flag = false;

        while (true) {
            if (temp.next == null) {
                System.out.println("到达链表的结尾处");
                break;
            }
            if (linkNode.no < temp.next.no) {
                System.out.println("找到了这个节点");
                break;
            } else if (linkNode.no == temp.next.no) {
                flag = true;
                System.out.println("节点排序值相等  不能添加");
                break;
            }
            temp = temp.next;
        }
        if (!flag) {
            linkNode.next = temp.next;
            temp.next = linkNode;
        }
    }

    /**
     * 更新某个节点
     *
     * @param linkNode
     */
    public void update(LinkNode linkNode) {
        LinkNode temp = headNode.next;

        if (temp == null) {
            System.out.println("空链表");
            return;
        }
        boolean flag = false;

        while (true) {
            if (temp.next == null) {
                System.out.println("到达链表的结尾处");
                break;
            }
            if (linkNode.no == temp.no) {
                flag = true;
                System.out.println("找到了这个节点");
                break;
            }
            temp = temp.next;
        }
        if (flag) {
            temp.data = linkNode.data;
        }
    }

    /**
     * 删除这个链表
     *
     * @param linkNode
     */
    public void delete(LinkNode linkNode) {
        LinkNode temp = headNode.next;

        if (temp == null) {
            System.out.println("空链表");
            return;
        }
        boolean flag = false;

        while (true) {
            if (temp.next == null) {
                System.out.println("到达链表的结尾处");
                break;
            }
            if (linkNode.no == temp.next.no) {
                flag = true;
                System.out.println("找到了这个节点");
                break;
            }
            temp = temp.next;
        }

        if (flag) {
            temp.next = temp.next.next;
        }
    }

    /**
     * 获取这个链表的有效数据的个数
     *
     * @return
     */
    public int length() {

        LinkNode temp = headNode.next;
        int length = 0;
        //空链表
        if (temp == null) {
            return 0;
        }

        while (true) {
            if (temp == null) {
                break;
            }
            length++;
            temp = temp.next;
        }
        return length;
    }

    /**
     * 遍历这个链表
     */
    public void list(LinkNode root) {
        LinkNode temp = root.next;
        if (temp == null) {
            System.out.println("空链表");
        }
        while (true) {
            if (temp == null) {
                System.out.println("已经取出链表中的所有元素");
                break;
            }
            System.out.println("打印"+temp);
            temp = temp.next;
        }
    }

    //获取第几个元素
    public LinkNode get(int index) {
        LinkNode temp = headNode.next;
        if (index > this.length()) {
            return null;
        }
        int count = 0;
        for (int i = 0; i < this.length(); i++) {
            count++;
            if (count == index) {
                return temp;
            } else {
                temp = temp.next;
            }
        }
        return null;
    }


    //寻找链表中的倒数第几个元素
    public LinkNode findReciprocal(int lastIndex) {
        int index = 0;
        int Differ = this.length() - lastIndex;  //6-2=4

        //倒数第length个元素 就直接返回  第一个元素
        if (Differ == index) {
            return headNode.next;
        } else if (lastIndex > this.length()) {
            return null;
        }
        LinkNode temp = headNode.next;
        while (true) {
            index++;
            if (Differ == index) {
                temp = temp.next;
                break;
            }
            temp = temp.next;
        }
        return temp;
    }

    //反转这个链表
    public void Reversal() {
        LinkNode<String> newNode = new LinkNode<>(0, "");
        LinkNode temp=newNode;
        for (int i = this.length(); i > 0; i--) {
            if(temp.next==null){
                System.out.println(this.get(i)+"***");
                temp.next=this.get(i);
//                System.out.println(newNode);
                temp=temp.next;
            }
        }
        System.out.println(newNode);
    }




}

class LinkNode<T> {

    public int no;
    public T data;
    public LinkNode<T> next;

    public LinkNode(int no, T data) {
        this.no = no;
        this.data = data;
    }

    @Override
    public String toString() {
        return "LinkNode{" +
                "no=" + no +
                ", data=" + data +
                ", next=" + next +
                '}';
    }
}

