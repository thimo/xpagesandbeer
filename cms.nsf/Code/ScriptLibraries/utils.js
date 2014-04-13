function x$(idTag, param) {
   idTag = idTag.replace(/:/gi, "\\:") + (param ? param : "");
   return($("#"+idTag));
}