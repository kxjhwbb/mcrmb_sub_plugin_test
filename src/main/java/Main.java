import com.mcrmb.CmdEvent;
import com.mcrmb.PayApi;
import com.mcrmb.event.McrmbPayEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("插件已载入...");
        getServer().getPluginManager().registerEvents(this,this); //注册本类为监听器
    }

    //支付监听
    @EventHandler
    public void test_pay_event(McrmbPayEvent payEvent){
        System.out.println("发生支付事件：");
        System.out.println("玩家名："+payEvent.getPlayer());
        System.out.println("点券数："+payEvent.getMoney());
        System.out.println("剩余余额："+payEvent.getLatestBalance());
        System.out.println("消费原因："+payEvent.getReason());
        System.out.println("消费结果："+payEvent.getResponse());
        System.out.println("是否公屏："+payEvent.isBroadcast());

    }

    //指令监听
    @EventHandler
    public void test_cmd_event(CmdEvent cmdEvent){
        cmdEvent.getCmd(); //第一个参数
        cmdEvent.getCmds(); //参数数组
        cmdEvent.getSender(); //命令发送人
        if(cmdEvent.getCmd().equalsIgnoreCase("nb")){
            // 用户执行了 /b nb xxx xxx 指令，插件处理的逻辑内容。
            System.out.println(cmdEvent.getSender().getName()+" 执行了指令 /b "+String.join(" ",cmdEvent.getCmds()));
            cmdEvent.getSender().sendMessage("你执行了指令 /b "+String.join(" ",cmdEvent.getCmds()));

        }else if(cmdEvent.getCmd().equalsIgnoreCase("balance") && cmdEvent.getCmds().length==2){
            // 用户执行了 /b balance 玩家名
            String playername = cmdEvent.getCmds()[1];
            checkmoney(playername);
        }else if(cmdEvent.getCmd().equalsIgnoreCase("forcepay") && cmdEvent.getCmds().length==5){
            // 用户执行了 /b forcepay <玩家名> <点券数> <消费原因> <是否公屏>
            // Pay(String playername,String point,String useto,boolean brocast)
            PayApi.Pay(
                    cmdEvent.getCmds()[1],
                    cmdEvent.getCmds()[2],
                    cmdEvent.getCmds()[3],
                    cmdEvent.getCmds()[4].equalsIgnoreCase("true")
            );
        }
    }

    //自己定义指令，本插件在plugins.yml定义了 /test
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        System.out.println(args[0]);
        // 使用指令： /test <玩家名> 调用mcrmb接口查询玩家信息
        if(args.length==1){
            String playername = args[0];
            checkmoney(playername);
        }
        return true;
    }

    //调用mcrmb的方法查询玩家信息
    private void checkmoney(String playername) {
        getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                System.out.println("查询玩家 "+playername);
                System.out.println("余额："+ PayApi.look(playername));
                System.out.println("累计消费："+PayApi.allpay(playername));
                System.out.println("累计充值："+PayApi.allcharge(playername));
            }
        });
    }

}
