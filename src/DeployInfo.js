import { useState } from "react";
import { useNavigate } from "react-router-dom";

const DeployInfo = () => {
    const[isPending,setIsPending] = useState(false);
    const[repo,setRepo] = useState('repo1');
    const[fromDateTime,setFromDateTime] = useState(undefined); //check what can be put instead of undefined
    const[toDateTime,setToDateTime] = useState(undefined); //check what can be put instead of undefined
    const navigate = useNavigate();

    const handleSearch = (e) =>{
        e.preventDefault(); 
        const data = {repo,fromDateTime,toDateTime};
        setIsPending(true);
        fetch('http://localhost:8080/deployments',{
            method:"POST",
            headers:{ "Content-type" : "application/json" },
            body: JSON.stringify(data)
        }).then(()=> {
            console.log("Submitted");
            setIsPending(false);
            navigate("/deployments", { replace: true });
        });
    };

    return ( 
        <div className="deploy-info">
            <h2>Deployments</h2>
            <form onSubmit={handleSearch}>
                
                <label>Repository</label>
                <select
                value={repo}
                onChange={(e)=> setRepo(e.target.value)}>
                    <option value="repo1">repo1</option>
                    <option value="repo2">repo2</option>
                    <option value="repo3">repo3</option>
                    <option value="repo4">repo4</option>
                    <option value="repo5">repo5</option>
                    <option value="repo6">repo6</option>
                </select>

                <label>From Date and time</label>
                <input 
                    type="datetime-local" 
                    value = {fromDateTime}
                    onChange={(e) => setFromDateTime(e.target.value)}
                />
                <label>To Date and time</label>
                <input 
                    type="datetime-local" 
                    value = {toDateTime}
                    onChange={(e) => setToDateTime(e.target.value)}
                />

                {!isPending && <button>Search</button>}
                {isPending && <button disabled>Loading data...</button>}
                
    
            </form>
        </div>
     );
}
 
export default DeployInfo;