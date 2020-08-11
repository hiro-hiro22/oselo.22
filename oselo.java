package OSELO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class oselo extends JFrame {

    //ボタン配列
    private JButton buttonArray[][];

    /* ボタン配列の内部表現 */
    /* 0: 石なし, 1: 黒, 2: 白 */
    /* 上記のbuttonArray[][]と連動させる */
    private int buttonFlag[][];

    private Container c;

    private ImageIcon blackIcon, whiteIcon;

    private JPanel p = null;

    private ImageIcon myIcon, yourIcon;

    /** ターンフラグ： 0:黒の番 1:白の番. */
    private int turnFlag = 0;

    private boolean NoColor;

    private Color color;

    public static void main(String[] args) {

        oselo os = new oselo();
        os.init();

        os.setVisible(true);
    }

    //初期化処理
    public oselo() {

        super();

        //ウィンドウを作成する
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに，正しく閉じるように設定する
        this.setTitle("オセロ"); //ウィンドウのタイトルを設定する
        this.setSize(500, 500); //ウィンドウのサイズを設定する
        c = this.getContentPane(); //フレームのペインを取得する
        c.setLayout(new GridLayout()); //自動レイアウトの設定を行わない

        //ボタン作成
        whiteIcon = new ImageIcon("White.png");
        blackIcon = new ImageIcon("Black.png");
        whiteIcon = new ImageIcon("src/White.png");
        blackIcon = new ImageIcon("src/Black.png");

        //ボタンの色わけ
        myIcon = whiteIcon;
        yourIcon = blackIcon;
        //何も石がない

        // ボタン配置用のパネルを作成
        // 等間隔に並ぶように、Gridレイアウトを指定
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(8, 8));

        // マージン取るための設定
        Border border = BorderFactory.createEmptyBorder(5, 15, 5, 15);
        p.setBorder(border);

        buttonArray = new JButton[8][8];
        buttonFlag = new int[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                // ボタンを作成して、パネルに追加
                JButton button = new JButton();

                //ボタンの背景色指定
                Color color = new Color(0, 159, 3);
                button.setBackground(color);

                //ボタンの枠線色
                LineBorder lineBorder = new LineBorder(Color.BLACK);
                button.setBorder(lineBorder);

                button.addActionListener(new myListener());

                button.setActionCommand(String.valueOf(x) + "," + String.valueOf(y));

                buttonArray[x][y] = button;
                p.add(button);

                buttonFlag[x][y] = 0;
            }

        }
        // パネルをフレームに追加
        c.add(p, BorderLayout.CENTER);

    }

    private void init() {
        // 全てのボタンのアイコンを非設定状態にする
        for (JButton[] buttons : buttonArray) {
            for (JButton button : buttons) {
                button.setIcon(null);
            }
        }

        //ボタンに初期配置のアイコン設定
        buttonArray[3][3].setIcon(whiteIcon);
        buttonArray[3][4].setIcon(blackIcon);
        buttonArray[4][3].setIcon(blackIcon);
        buttonArray[4][4].setIcon(whiteIcon);
        buttonFlag[3][3] = 2;
        buttonFlag[3][4] = 1;
        buttonFlag[4][3] = 1;
        buttonFlag[4][4] = 2;

        //ボタンの色わけ
        myIcon = whiteIcon;
        yourIcon = blackIcon;

    }


    // stone_color: "black" or "white" */
    //putStone(x, y);とすることで、黒石と白石が交互に(x, y)に打たれる */

    private void putStone(int x, int y) {

        // 石が置かれた場所の周り状態を確認
        int changeCount = 0;
        for (Locations l : Locations.values()) {

            // 確認対象の位置を取得
            int posX = x + l.x;
            int posY = y + l.y;


            // 確認対象の場所がマイナス値もしくは9（盤の外）の場合、対象外
            if (posX < 0 || posX > 7) {
                continue;
            }
            if (posY < 0 || posY > 7) {
                continue;
            }

            // 確認対象の盤の状態を取得
            int state = buttonFlag[posX][posY];

            // 石が置かれていない場合
            // または自分の石が置かれている場合は処理対象外
            if (state == 0 || state == turnFlag+1) {
                continue;
            }

            // 処理中の方向の石の状態を変更する
            switch(l) {
                case North:
                    changeCount += putStoneNorth(x, y);
                    break;
                case Northeast:
                    changeCount += putStoneNorthEast(x, y);
                    break;
                case East:
                    changeCount += putStoneEast(x, y);
                    break;
                case Southeast:
                    changeCount += putStoneSouthEast(x, y);
                    break;
                case South:
                    changeCount += putStoneSouth(x, y);
                    break;
                case Southwest:
                    changeCount += putStoneSouthWest(x, y);
                    break;
                case West:
                    changeCount += putStoneWest(x, y);
                    break;
                case Northwest:
                    changeCount += putStoneNorthWest(x, y);
                    break;

                default:
                    break;
            }
        }

        // 石をひっくり返せたらクリック位置に石を配置
        if (changeCount > 0) {
            changeStone(x, y);

            if (turnFlag == 0) {
                /* ---- 黒石の番 ---- */
                /* 次は白石の番になるので，turnFlagを1にセット */
                turnFlag = 1;
            } else if (turnFlag == 1) {
                /* ---- 白石の番 ---- */
                /* 次は黒石の番になるので，turnFlagを0にセット */
                turnFlag = 0;
            }
        }
    }

    /**
     * 北方向に石を置く
     * @param x 石を置いた横位置
     * @param y 石を置いた縦位置
     * @return 変更した石の数
     */
    private int putStoneNorth(int x, int y) {

        List<Location> tempList = new ArrayList<Location>();

        for (; y > 0; y--) {
            // マスの状態を取得
            int state = buttonFlag[x][y];

            // 敵の石の場合、リストに追加
            if ((turnFlag == 0 && state == 2)
                || (turnFlag == 1 && state == 1)) {
                tempList.add(new Location(x, y));
            }

            // 自分の石になった時点で更新処理を行う
            if ((turnFlag == 0 && state == 1)
                    || (turnFlag == 1 && state == 2)) {
                for (Location l : tempList) {
                    changeStone(l.x, l.y);
                }
                return tempList.size();
            }
        }

        return 0;
    }

    /**
     * 北東方向に石を置く
     * @param x 石を置いた横位置
     * @param y 石を置いた縦位置
     * @return 変更した石の数
     */
    private int putStoneNorthEast(int x, int y) {

        List<Location> tempList = new ArrayList<Location>();

        for (; x < 8 && y > 0; x++, y--) {
            // 石の状態を取得
            int state = buttonFlag[x][y];

            // 敵の石の場合、リストに追加
            if ((turnFlag == 0 && state == 2)
                || (turnFlag == 1 && state == 1)) {
                tempList.add(new Location(x, y));
            }

            // 自分の石になった時点で更新処理を行う
            if ((turnFlag == 0 && state == 1)
                    || (turnFlag == 1 && state == 2)) {
                for (Location l : tempList) {
                    changeStone(l.x, l.y);
                }
                return tempList.size();
            }
        }

        return 0;
    }

    /**
     * 東方向に石を置く
     * @param x 石を置いた横位置
     * @param y 石を置いた縦位置
     * @return 変更した石の数
     */
    private int putStoneEast(int x, int y) {

        List<Location> tempList = new ArrayList<Location>();

        for (; x < 8; x++) {
            // 石の状態を取得
            int state = buttonFlag[x][y];

            // 敵の石の場合、リストに追加
            if ((turnFlag == 0 && state == 2)
                || (turnFlag == 1 && state == 1)) {
                tempList.add(new Location(x, y));
            }

            // 自分の石になった時点で更新処理を行う
            if ((turnFlag == 0 && state == 1)
                    || (turnFlag == 1 && state == 2)) {
                for (Location l : tempList) {
                    changeStone(l.x, l.y);
                }
                return tempList.size();
            }
        }

        return 0;
    }

    /**
     * 南東方向に石を置く
     * @param x 石を置いた横位置
     * @param y 石を置いた縦位置
     * @return 変更した石の数
     */
    private int putStoneSouthEast(int x, int y) {

        List<Location> tempList = new ArrayList<Location>();

        for (; x < 8 && y < 8; x++, y++) {
            // 石の状態を取得
            int state = buttonFlag[x][y];

            // 敵の石の場合、リストに追加
            if ((turnFlag == 0 && state == 2)
                || (turnFlag == 1 && state == 1)) {
                tempList.add(new Location(x, y));
            }

            // 自分の石になった時点で更新処理を行う
            if ((turnFlag == 0 && state == 1)
                    || (turnFlag == 1 && state == 2)) {
                for (Location l : tempList) {
                    changeStone(l.x, l.y);
                }
                return tempList.size();
            }
        }

        return 0;
    }

    /**
     * 南方向に石を置く
     * @param x 石を置いた横位置
     * @param y 石を置いた縦位置
     * @return 変更した石の数
     */
    private int putStoneSouth(int x, int y) {

        List<Location> tempList = new ArrayList<Location>();

        for (; y < 8; y++) {
            // 石の状態を取得
            int state = buttonFlag[x][y];

            // 敵の石の場合、リストに追加
            if ((turnFlag == 0 && state == 2)
                || (turnFlag == 1 && state == 1)) {
                tempList.add(new Location(x, y));
            }

            // 自分の石になった時点で更新処理を行う
            if ((turnFlag == 0 && state == 1)
                    || (turnFlag == 1 && state == 2)) {
                for (Location l : tempList) {
                    changeStone(l.x, l.y);
                }
                return tempList.size();
            }
        }

        return 0;
    }


    /**
     * 南西方向に石を置く
     * @param x 石を置いた横位置
     * @param y 石を置いた縦位置
     * @return 変更した石の数
     */
    private int putStoneSouthWest(int x, int y) {

        List<Location> tempList = new ArrayList<Location>();

        for (; x > 0 && y < 8; x--, y++) {
            // 石の状態を取得
            int state = buttonFlag[x][y];

            // 敵の石の場合、リストに追加
            if ((turnFlag == 0 && state == 2)
                || (turnFlag == 1 && state == 1)) {
                tempList.add(new Location(x, y));
            }

            // 自分の石になった時点で更新処理を行う
            if ((turnFlag == 0 && state == 1)
                    || (turnFlag == 1 && state == 2)) {
                for (Location l : tempList) {
                    changeStone(l.x, l.y);
                }
                return tempList.size();
            }
        }

        return 0;
    }

    /**
     * 西方向に石を置く
     * @param x 石を置いた横位置
     * @param y 石を置いた縦位置
     * @return 変更した石の数
     */
    private int putStoneWest(int x, int y) {

        List<Location> tempList = new ArrayList<Location>();

        for (; x > 0; x--) {
            // 石の状態を取得
            int state = buttonFlag[x][y];

            // 敵の石の場合、リストに追加
            if ((turnFlag == 0 && state == 2)
                || (turnFlag == 1 && state == 1)) {
                tempList.add(new Location(x, y));
            }

            // 自分の石になった時点で更新処理を行う
            if ((turnFlag == 0 && state == 1)
                    || (turnFlag == 1 && state == 2)) {
                for (Location l : tempList) {
                    changeStone(l.x, l.y);
                }
                return tempList.size();
            }
        }

        return 0;
    }

    /**
     * 北西方向に石を置く
     * @param x 石を置いた横位置
     * @param y 石を置いた縦位置
     * @return 変更した石の数
     */
    private int putStoneNorthWest(int x, int y) {

        List<Location> tempList = new ArrayList<Location>();

        for (; x > 0 && y > 0; x--, y--) {
            // 石の状態を取得
            int state = buttonFlag[x][y];

            // 敵の石の場合、リストに追加
            if ((turnFlag == 0 && state == 2)
                || (turnFlag == 1 && state == 1)) {
                tempList.add(new Location(x, y));
            }

            // 自分の石になった時点で更新処理を行う
            if ((turnFlag == 0 && state == 1)
                    || (turnFlag == 1 && state == 2)) {
                for (Location l : tempList) {
                    changeStone(l.x, l.y);
                }
                return tempList.size();
            }
        }

        return 0;
    }

    /**
     * 石の状態を変更する
     * @param x 変更する石の横位置
     * @param y 変更する石の縦位置
     */
    private void changeStone(int x, int y) {
        if (turnFlag == 0) {
            /* ---- 黒石の番 ---- */
            /* 画像をセット */
            buttonArray[x][y].setIcon(blackIcon);
            /* buttonFlagをセット */
            buttonFlag[x][y] = 1;
        } else if (turnFlag == 1) {
            /* ---- 白石の番 ---- */
            /* 画像をセット */
            buttonArray[x][y].setIcon(whiteIcon);
            /* buttonFlagをセット */
            buttonFlag[x][y] = 2;
        }
    }

    public class myListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println(e);

            /* 押されたボタンのcommandを取得 */
            String cmd = e.getActionCommand();

            /* commandは"x,y"という名前になっているため，splitによりxとyを分離 */
            String[] pos = cmd.split(",", 0);
            int x = Integer.parseInt(pos[0]);
            int y = Integer.parseInt(pos[1]);

            /* (x, y)に石が置かれていなかったら，(x, y)に石を置く */
            if (buttonFlag[x][y] == 0) {
                putStone(x, y);
            }
        }
    }

    /**
     * 位置情報管理クラス.
     */
    public class Location {

        /** 横位置. */
        public int x;
        /** 縦位置. */
        public int y;

        /**
         * コンストラクタ.
         * @param x 横位置
         * @param y 縦位置
         */
        public Location(int x, int y) {
            super();
            this.x = x;
            this.y = y;
        }
    }

    /**
     * 位置情報の列挙型.
     */
    private enum Locations {
        North(0, -1),
        Northeast(1, -1),
        East(1, 0),
        Southeast(1, 1),
        South(0, 1),
        Southwest(-1, 1),
        West(-1, 0),
        Northwest(-1, -1);

        private int x;
        private int y;

        Locations(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}