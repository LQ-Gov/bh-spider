import { observable } from "mobx";
import axios from 'axios';

export default class ModuleStore {
    httpclient;

    @observable reacordCount = 0
    @observable modules = []
    @observable loading = false


    constructor() {
        this.httpclient = axios;
    }

    ping = ()=>{
        console.log("pong");
    }


    load = async (page, size) => {
        this.loading=true
        var response = await httpclient.get("/modules", { start: page * size, size: size })
        this.reacordCount = response.count
        this.modules = response.data
        this.loading = false
    }

    remove = async (id,index)=>{
        this.loading = true
        var response = await httpclient.delete("/module/"+id);
        modules.splice(1,1);
        this.loading = false
    }

}